import asyncio
import logging
import uvicorn

from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from config import settings
from schemas import HealthResponse
from model import get_model
from consumer import start_consumer

# ── Logging setup ──────────────────────────────────────────────────────────
logging.basicConfig(
    level  = settings.LOG_LEVEL,
    format = "%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
logger = logging.getLogger(__name__)


# ── Startup / Shutdown lifecycle ───────────────────────────────────────────
@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    On startup:  preload models + start Kafka consumer
    On shutdown: graceful cleanup
    """
    logger.info("=== AI Service starting ===")

    # Preload models (DenseNet + EfficientNet) into memory
    logger.info("Loading ensemble models...")
    get_model()
    logger.info("Models loaded ✓")

    # Start Kafka consumer in background
    consumer_task = asyncio.create_task(start_consumer())
    logger.info("Kafka consumer task started ✓")
    logger.info("=== AI Service ready ===")

    yield  # app is running

    # Shutdown
    logger.info("=== AI Service shutting down ===")
    consumer_task.cancel()
    try:
        await consumer_task
    except asyncio.CancelledError:
        pass
    logger.info("=== AI Service stopped ===")


# ── FastAPI app ────────────────────────────────────────────────────────────
app = FastAPI(
    title       = "AI Radio Service",
    description = "Chest X-Ray classification microservice — DenseNet-121 + EfficientNet-B3 ensemble",
    version     = "1.0.0",
    lifespan    = lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins  = ["*"],
    allow_methods  = ["*"],
    allow_headers  = ["*"],
)


# ── Endpoints ──────────────────────────────────────────────────────────────
@app.get("/health", response_model=HealthResponse, tags=["Monitoring"])
async def health_check():
    """
    Health check endpoint.
    Used by Docker Compose healthcheck and other services.
    """
    try:
        model = get_model()
        return HealthResponse(
            status  = "UP",
            models  = "DenseNet-121 + EfficientNet-B3",
            device  = str(model.device),
        )
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"Models not ready: {e}")


@app.get("/", tags=["Info"])
async def root():
    return {
        "service" : "AI Radio Service",
        "version" : "1.0.0",
        "status"  : "running",
        "topics"  : {
            "consumes" : settings.KAFKA_TOPIC_CONSUME,
            "produces" : settings.KAFKA_TOPIC_PRODUCE,
        }
    }


# ── Entrypoint ─────────────────────────────────────────────────────────────
if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host    = settings.APP_HOST,
        port    = settings.APP_PORT,
        reload  = False,
        workers = 1,   # single worker — models are not fork-safe
    )
