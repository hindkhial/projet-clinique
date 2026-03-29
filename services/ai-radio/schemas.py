from pydantic import BaseModel
from typing import Optional


# ── CONSUMED from Kafka ────────────────────────────────────────────────────
class RadioUploadedEvent(BaseModel):
    """
    Deserialized from Kafka topic: radio.uploaded
    Published by: Radio Service (Spring Boot).

    Spring Boot serializes to camelCase by default.
    We accept both camelCase and snake_case to be safe.

    Example payload from your Radio Service:
    {
        "id": 7,
        "patientId": 1,
        "radiologueId": 5,
        "dateRadio": "2026-02-13T10:30:00",
        "fichierRadioUrl": "radio-bucket/radio_7_uuid.png",
        "notes": "Radio thorax"
    }
    """
    # camelCase (Spring Boot default)
    id:              Optional[int]  = None
    patientId:       Optional[str]  = None
    radiologueId:    Optional[str]  = None
    fichierRadioUrl: Optional[str]  = None
    dateRadio:       Optional[str]  = None
    notes:           Optional[str]  = ""

    # snake_case (proto style — fallback)
    radio_id:          Optional[str] = None
    patient_id:        Optional[str] = None
    radiologue_id:     Optional[str] = None
    fichier_radio_url: Optional[str] = None
    date_radio:        Optional[str] = None

    @property
    def get_radio_id(self) -> str:
        return self.radio_id or str(self.id) or ""

    @property
    def get_patient_id(self) -> str:
        return self.patient_id or str(self.patientId) or ""

    @property
    def get_radiologue_id(self) -> str:
        return self.radiologue_id or str(self.radiologueId) or ""

    @property
    def get_fichier_radio_url(self) -> str:
        return self.fichier_radio_url or self.fichierRadioUrl or ""

    @property
    def get_date_radio(self) -> str:
        return self.date_radio or str(self.dateRadio) or ""


# ── PUBLISHED to Kafka ─────────────────────────────────────────────────────
class AIPredictionEvent(BaseModel):
    """
    Published to Kafka topic: ai.prediction.done
    Consumed by: Consultation Service (Spring Boot)
    """
    radio_id:                str
    patient_id:              str
    radiologue_id:           str
    fichier_radio_url:       str
    heatmap_url:             str
    type_maladie:            str    # COVID19 | Normal | Pneumonia | Tuberculosis
    confidence:              float
    risk_level:              str    # LOW | MEDIUM | HIGH
    probabilities:           dict
    model_agreement:         bool
    date_radio:              str
    densenet_prediction:     str
    densenet_confidence:     float
    efficientnet_prediction: str
    efficientnet_confidence: float


# ── Risk level ─────────────────────────────────────────────────────────────
def compute_risk_level(type_maladie: str, confidence: float) -> str:
    if type_maladie == "Normal":
        return "LOW"
    if confidence >= 0.85:
        return "HIGH"
    if confidence >= 0.60:
        return "MEDIUM"
    return "LOW"


# ── Health check ───────────────────────────────────────────────────────────
class HealthResponse(BaseModel):
    status:  str
    models:  str
    device:  str
    version: str = "1.0.0"
