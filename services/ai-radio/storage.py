import logging
import uuid
from io import BytesIO

from minio import Minio
from minio.error import S3Error

from config import settings

logger = logging.getLogger(__name__)


class MinIOClient:
    """
    Handles all MinIO operations:
    - Download radio images from radio-bucket
    - Upload Grad-CAM heatmaps to heatmap-bucket
    """

    def __init__(self):
        self.client = Minio(
            endpoint   = settings.MINIO_ENDPOINT,
            access_key = settings.MINIO_ACCESS_KEY,
            secret_key = settings.MINIO_SECRET_KEY,
            secure     = settings.MINIO_SECURE,
        )
        self._ensure_bucket(settings.MINIO_BUCKET_HEATMAP)
        logger.info(f"MinIO client connected to {settings.MINIO_ENDPOINT} ✓")

    def _ensure_bucket(self, bucket_name: str) -> None:
        """Create bucket if it doesn't exist."""
        try:
            if not self.client.bucket_exists(bucket_name):
                self.client.make_bucket(bucket_name)
                logger.info(f"Created bucket: {bucket_name}")
        except S3Error as e:
            logger.error(f"Failed to ensure bucket {bucket_name}: {e}")
            raise

    def download_radio(self, fichier_radio_url: str) -> bytes:
        """
        Download radio image from MinIO.

        Args:
            fichier_radio_url: value from Kafka event, format is either:
                - "radio-bucket/radio_7_uuid.png"  ← bucket/key (your Radio Service)
                - "radio_7_uuid.png"               ← key only

        Returns:
            Raw image bytes
        """
        # Parse bucket and object key from fichierRadioUrl
        # Format: "radio-bucket/radio_7_uuid.png"
        if "/" in fichier_radio_url:
            parts      = fichier_radio_url.split("/", 1)
            bucket     = parts[0]
            object_key = parts[1]
        else:
            # fallback: use configured bucket
            bucket     = settings.MINIO_BUCKET_RADIO
            object_key = fichier_radio_url

        try:
            response = self.client.get_object(bucket, object_key)
            data     = response.read()
            response.close()
            response.release_conn()
            logger.info(f"Downloaded: {bucket}/{object_key} ({len(data):,} bytes)")
            return data
        except S3Error as e:
            logger.error(f"Failed to download {bucket}/{object_key}: {e}")
            raise

    def upload_heatmap(self, image_bytes: bytes, radio_id: str) -> str:
        """
        Upload Grad-CAM heatmap to heatmap-bucket.

        Args:
            image_bytes : JPEG bytes of heatmap overlay
            radio_id    : used to generate unique filename

        Returns:
            object_key: MinIO key for the uploaded heatmap
        """
        object_key   = f"heatmaps/{radio_id}_gradcam.jpg"
        image_stream = BytesIO(image_bytes)

        try:
            self.client.put_object(
                bucket_name  = settings.MINIO_BUCKET_HEATMAP,
                object_name  = object_key,
                data         = image_stream,
                length       = len(image_bytes),
                content_type = "image/jpeg",
            )
            logger.info(f"Uploaded heatmap: {object_key}")
            return object_key
        except S3Error as e:
            logger.error(f"Failed to upload heatmap: {e}")
            raise


# Singleton
_minio_instance: MinIOClient | None = None


def get_minio() -> MinIOClient:
    global _minio_instance
    if _minio_instance is None:
        _minio_instance = MinIOClient()
    return _minio_instance
