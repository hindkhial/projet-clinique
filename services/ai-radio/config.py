from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # Kafka
    KAFKA_BOOTSTRAP_SERVERS: str = "kafka:9092"
    KAFKA_CONSUMER_GROUP: str    = "ai-service-group"
    KAFKA_TOPIC_CONSUME: str     = "radio.uploaded"
    KAFKA_TOPIC_PRODUCE: str     = "ai.prediction.done"

    # MinIO — matches your existing docker-compose values
    MINIO_ENDPOINT: str          = "minio:9000"
    MINIO_ACCESS_KEY: str
    MINIO_SECRET_KEY: str
    MINIO_BUCKET_RADIO: str      = "radio-bucket"       # same bucket as radio-service
    MINIO_BUCKET_HEATMAP: str    = "heatmap-bucket"
    MINIO_SECURE: bool           = False

    # Model weights (mounted via Docker volume)
    MODEL_DENSENET_PATH: str     = "/app/weights/DenseNet121_final.pt"
    MODEL_EFFNET_PATH: str       = "/app/weights/EfficientNetB3_final.pt"
    DEVICE: str                  = "cpu"               # cpu for local, cuda for GPU server

    # App
    APP_HOST: str                = "0.0.0.0"
    APP_PORT: int                = 8001
    LOG_LEVEL: str               = "INFO"

    class Config:
        env_file = ".env"


settings = Settings()
