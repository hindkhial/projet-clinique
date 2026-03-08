import json
import logging
import asyncio
from datetime import datetime

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
from google.protobuf.json_format import MessageToDict

from config import settings
from schemas import RadioUploadedEvent, AIPredictionEvent, compute_risk_level
from model import get_model
from storage import get_minio
from preprocess import preprocess_image
from gradcam import generate_overlay, image_to_bytes

logger = logging.getLogger(__name__)


async def process_radio_event(event: RadioUploadedEvent, producer: AIOKafkaProducer) -> None:
    """
    Full AI pipeline for one radio upload event:
    1. Download image from MinIO
    2. Preprocess (CLAHE)
    3. Run ensemble inference
    4. Generate Grad-CAM heatmap
    5. Upload heatmap to MinIO
    6. Publish ai.prediction.done to Kafka
    """
    logger.info(f"Processing radio_id={event.get_radio_id} patient_id={event.get_patient_id}")

    try:
        minio = get_minio()
        model = get_model()

        # ── 1. Download image ───────────────────────────────────
        image_bytes = minio.download_radio(event.get_fichier_radio_url)

        # ── 2. Preprocess ───────────────────────────────────────
        tensor, display = preprocess_image(image_bytes)

        # ── 3. Ensemble inference ───────────────────────────────
        result = model.predict(tensor)
        logger.info(
            f"Prediction: {result.pred_class} "
            f"({result.confidence:.1%}) "
            f"agreement={result.model_agreement}"
        )

        # ── 4. Generate Grad-CAM overlay ────────────────────────
        # Use DenseNet heatmap as primary (higher accuracy model)
        overlay     = generate_overlay(display, result.cam_densenet, alpha=0.45)
        heatmap_bytes = image_to_bytes(overlay, fmt="JPEG")

        # ── 5. Upload heatmap to MinIO ──────────────────────────
        heatmap_key = minio.upload_heatmap(heatmap_bytes, event.get_radio_id)

        # ── 6. Build and publish prediction event ───────────────
        risk_level = compute_risk_level(result.pred_class, result.confidence)

        prediction_event = AIPredictionEvent(
            radio_id                = event.get_radio_id,
            patient_id              = event.get_patient_id,
            radiologue_id           = event.get_radiologue_id,
            fichier_radio_url       = event.get_fichier_radio_url,
            heatmap_url             = heatmap_key,
            type_maladie            = result.pred_class,
            confidence              = result.confidence,
            risk_level              = risk_level,
            probabilities           = result.probabilities,
            model_agreement         = result.model_agreement,
            date_radio              = event.get_date_radio,
            densenet_prediction     = result.densenet_prediction,
            densenet_confidence     = result.densenet_confidence,
            efficientnet_prediction = result.efficientnet_prediction,
            efficientnet_confidence = result.efficientnet_confidence,
        )

        await producer.send(
            settings.KAFKA_TOPIC_PRODUCE,
            key   = event.get_radio_id.encode(),
            value = prediction_event.model_dump_json().encode(),
        )

        logger.info(
            f"Published ai.prediction.done for radio_id={event.get_radio_id} "
            f"→ {result.pred_class} | risk={risk_level}"
        )

    except Exception as e:
        logger.error(f"Failed to process radio_id={event.get_radio_id}: {e}", exc_info=True)
        # Don't re-raise — log and continue consuming next messages


async def start_consumer() -> None:
    """
    Start Kafka consumer loop.
    Consumes from radio.uploaded, publishes to ai.prediction.done.
    """
    consumer = AIOKafkaConsumer(
        settings.KAFKA_TOPIC_CONSUME,
        bootstrap_servers = settings.KAFKA_BOOTSTRAP_SERVERS,
        group_id          = settings.KAFKA_CONSUMER_GROUP,
        auto_offset_reset = "earliest",
        enable_auto_commit= True,
        value_deserializer= lambda v: v,   # raw bytes — we parse manually
    )

    producer = AIOKafkaProducer(
        bootstrap_servers  = settings.KAFKA_BOOTSTRAP_SERVERS,
        value_serializer   = lambda v: v,  # already bytes
        acks               = "all",
        enable_idempotence = True,
    )

    await consumer.start()
    await producer.start()
    logger.info(f"Kafka consumer started → listening on '{settings.KAFKA_TOPIC_CONSUME}'")

    try:
        async for msg in consumer:
            try:
                # Deserialize JSON payload from Radio Service
                payload = json.loads(msg.value.decode("utf-8"))
                event   = RadioUploadedEvent(**payload)
                await process_radio_event(event, producer)
            except Exception as e:
                logger.error(f"Failed to parse event: {e}", exc_info=True)
    finally:
        await consumer.stop()
        await producer.stop()
        logger.info("Kafka consumer stopped.")
