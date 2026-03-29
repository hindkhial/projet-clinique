import logging

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

from config import settings
from schemas import RadioUploadedEvent, compute_risk_level
from model import get_model
from storage import get_minio
from preprocess import preprocess_image
from gradcam import generate_overlay, image_to_bytes
from proto_parser import parse_radio_uploaded_event, serialize_ai_prediction_event

logger = logging.getLogger(__name__)


async def process_radio_event(event: RadioUploadedEvent, producer: AIOKafkaProducer) -> None:
    radio_id      = event.get_radio_id
    patient_id    = event.get_patient_id
    radiologue_id = event.get_radiologue_id
    fichier_url   = event.get_fichier_radio_url
    date_radio    = event.get_date_radio

    logger.info(f"Processing radio_id={radio_id} patient_id={patient_id}")
    logger.info(f"File: {fichier_url}")

    try:
        minio = get_minio()
        model = get_model()

        # 1. Download image from MinIO
        image_bytes = minio.download_radio(fichier_url)

        # 2. Preprocess (CLAHE)
        tensor, display = preprocess_image(image_bytes)

        # 3. Ensemble inference
        result = model.predict(tensor)
        logger.info(
            f"Prediction: {result.pred_class} "
            f"({result.confidence:.1%}) "
            f"agreement={result.model_agreement}"
        )

        # 4. Grad-CAM heatmap
        overlay       = generate_overlay(display, result.cam_densenet, alpha=0.45)
        heatmap_bytes = image_to_bytes(overlay, fmt="JPEG")

        # 5. Upload heatmap to MinIO
        heatmap_key = minio.upload_heatmap(heatmap_bytes, radio_id)

        # 6. Serialize as Protobuf and publish
        risk_level    = compute_risk_level(result.pred_class, result.confidence)
        proto_bytes   = serialize_ai_prediction_event(
            radio_id                = radio_id,
            patient_id              = patient_id,
            radiologue_id           = radiologue_id,
            fichier_radio_url       = fichier_url,
            heatmap_url             = heatmap_key,
            type_maladie            = result.pred_class,
            confidence              = result.confidence,
            risk_level              = risk_level,
            model_agreement         = result.model_agreement,
            date_radio              = date_radio,
            densenet_prediction     = result.densenet_prediction,
            densenet_confidence     = result.densenet_confidence,
            efficientnet_prediction = result.efficientnet_prediction,
            efficientnet_confidence = result.efficientnet_confidence,
            probabilities           = result.probabilities,
        )

        await producer.send(
            settings.KAFKA_TOPIC_PRODUCE,
            key   = radio_id.encode(),
            value = proto_bytes,
        )

        logger.info(
            f"✅ Published ai.prediction.done (Protobuf) → "
            f"{result.pred_class} | confidence={result.confidence:.1%} | risk={risk_level}"
        )

    except Exception as e:
        logger.error(f"Failed to process radio_id={radio_id}: {e}", exc_info=True)


async def start_consumer() -> None:
    consumer = AIOKafkaConsumer(
        settings.KAFKA_TOPIC_CONSUME,
        bootstrap_servers  = settings.KAFKA_BOOTSTRAP_SERVERS,
        group_id           = settings.KAFKA_CONSUMER_GROUP,
        auto_offset_reset  = "earliest",
        enable_auto_commit = True,
        value_deserializer = lambda v: v,  # raw bytes
    )

    producer = AIOKafkaProducer(
        bootstrap_servers  = settings.KAFKA_BOOTSTRAP_SERVERS,
        value_serializer   = lambda v: v,  # raw bytes
        acks               = "all",
        enable_idempotence = True,
    )

    await consumer.start()
    await producer.start()
    logger.info(f"Kafka consumer started → listening on '{settings.KAFKA_TOPIC_CONSUME}'")

    try:
        async for msg in consumer:
            try:
                # Deserialize incoming Protobuf from Radio Service
                parsed = parse_radio_uploaded_event(msg.value)
                event  = RadioUploadedEvent(**parsed)
                await process_radio_event(event, producer)
            except Exception as e:
                logger.error(f"Failed to process message: {e}", exc_info=True)
    finally:
        await consumer.stop()
        await producer.stop()
        logger.info("Kafka consumer stopped.")