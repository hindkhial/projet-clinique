# AI Radio Microservice

**DenseNet-121 + EfficientNet-B3 Ensemble — Chest X-Ray Classifier**

## Overview

This microservice integrates into the hospital microservices architecture as an event-driven AI inference engine.

```
radio.uploaded (Kafka)
        ↓
   AI Service
   ├── Download image from MinIO
   ├── CLAHE preprocessing
   ├── DenseNet-121 inference
   ├── EfficientNet-B3 inference
   ├── Soft-voting ensemble
   ├── Grad-CAM heatmap generation
   └── Upload heatmap to MinIO
        ↓
ai.prediction.done (Kafka)
        ↓
 Consultation Service
```

## Setup

### 1. Add model weights

Download your trained models from Google Drive and place them in the `weights/` folder:

```
services/ai-service/
└── weights/
    ├── DenseNet121_final.pt      ← 94.63% val accuracy
    └── EfficientNetB3_final.pt   ← 92.48% val accuracy
```

### 2. Add to docker-compose.yml

Copy the contents of `docker-compose-addition.yml` into your main `docker-compose.yml`.

### 3. Place service folder

```
services/
├── auth/
├── patient/
├── radio/
├── consultation/
├── ai-service/          ← place this folder here
│   ├── main.py
│   ├── model.py
│   ├── consumer.py
│   ├── gradcam.py
│   ├── preprocess.py
│   ├── storage.py
│   ├── schemas.py
│   ├── config.py
│   ├── requirements.txt
│   ├── Dockerfile
│   └── weights/
│       ├── DenseNet121_final.pt
│       └── EfficientNetB3_final.pt
└── ...
```

### 4. Start

```bash
docker-compose up --build ai-service
```

## Kafka Events

### Consumed: `radio.uploaded`

```json
{
  "radio_id": "uuid",
  "patient_id": "uuid",
  "radiologue_id": "uuid",
  "fichier_radio_url": "radios/filename.jpg",
  "date_radio": "2026-02-26T10:00:00",
  "notes": "optional notes"
}
```

### Produced: `ai.prediction.done`

```json
{
  "radio_id": "uuid",
  "patient_id": "uuid",
  "radiologue_id": "uuid",
  "fichier_radio_url": "radios/filename.jpg",
  "heatmap_url": "heatmaps/uuid_gradcam.jpg",
  "type_maladie": "COVID19",
  "confidence": 0.943,
  "risk_level": "HIGH",
  "probabilities": {
    "COVID19": 0.943,
    "Normal": 0.021,
    "Pneumonia": 0.019,
    "Tuberculosis": 0.017
  },
  "model_agreement": true,
  "densenet_prediction": "COVID19",
  "densenet_confidence": 0.961,
  "efficientnet_prediction": "COVID19",
  "efficientnet_confidence": 0.925,
  "date_radio": "2026-02-26T10:00:00"
}
```

## Risk Level Logic

| Condition | Risk Level |
|---|---|
| type_maladie = Normal | LOW |
| confidence ≥ 0.85 (disease) | HIGH |
| confidence ≥ 0.60 (disease) | MEDIUM |
| confidence < 0.60 (disease) | LOW |

## Health Check

```
GET http://localhost:8001/health
```

```json
{
  "status": "UP",
  "models": "DenseNet-121 + EfficientNet-B3",
  "device": "cpu",
  "version": "1.0.0"
}
```

## Model Performance

| Model | Val Accuracy | AUROC | Kappa |
|---|---|---|---|
| DenseNet-121 | 94.63% | 0.9952 | 0.9238 |
| EfficientNet-B3 | 92.48% | — | — |
| **Ensemble** | **~95-96%** | — | — |
