import torch
import torch.nn as nn
import torch.nn.functional as F
import torchvision.models as models
import timm
import numpy as np
import logging
from dataclasses import dataclass

from config import settings
from gradcam import GradCAM

logger = logging.getLogger(__name__)

CLASSES = ["COVID19", "Normal", "Pneumonia", "Tuberculosis"]


@dataclass
class PredictionResult:
    pred_class:              str
    confidence:              float
    probabilities:           dict
    densenet_prediction:     str
    densenet_confidence:     float
    efficientnet_prediction: str
    efficientnet_confidence: float
    model_agreement:         bool
    cam_densenet:            np.ndarray
    cam_efficientnet:        np.ndarray


# ── DenseNet-121 ───────────────────────────────────────────────────────────
class DenseNet121Classifier(nn.Module):
    def __init__(self, num_classes: int = 4):
        super().__init__()
        backbone        = models.densenet121(weights=None)
        self.features   = backbone.features
        in_features     = backbone.classifier.in_features
        self.classifier = nn.Sequential(
            nn.AdaptiveAvgPool2d((1, 1)),
            nn.Flatten(),
            nn.Dropout(p=0.4),
            nn.Linear(in_features, 256),
            nn.ReLU(inplace=True),
            nn.Dropout(p=0.3),
            nn.Linear(256, num_classes),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        # inplace=False required for Grad-CAM backward hook
        out = F.relu(self.features(x), inplace=False)
        return self.classifier(out)


# ── EfficientNet-B3 ────────────────────────────────────────────────────────
class EfficientNetB3Classifier(nn.Module):
    def __init__(self, num_classes: int = 4):
        super().__init__()
        self.backbone = timm.create_model(
            'efficientnet_b3',
            pretrained=False,
            num_classes=0,
            global_pool='avg'
        )
        self.classifier = nn.Sequential(
            nn.Dropout(p=0.3),
            nn.Linear(self.backbone.num_features, 256),
            nn.ReLU(inplace=True),
            nn.Dropout(p=0.2),
            nn.Linear(256, num_classes),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.classifier(self.backbone(x))


# ── Ensemble ───────────────────────────────────────────────────────────────
class EnsembleModel:
    """
    Loads both models, runs ensemble inference, and generates Grad-CAM.
    Singleton — loaded once at startup.
    """

    def __init__(self):
        self.device = torch.device(settings.DEVICE)
        logger.info(f"Loading models on device: {self.device}")

        # Load DenseNet-121
        self.densenet = DenseNet121Classifier(num_classes=4).to(self.device)
        self.densenet.load_state_dict(
            torch.load(settings.MODEL_DENSENET_PATH, map_location=self.device))
        self.densenet.eval()
        logger.info("DenseNet-121 loaded ✓")

        # Load EfficientNet-B3
        self.efficientnet = EfficientNetB3Classifier(num_classes=4).to(self.device)
        self.efficientnet.load_state_dict(
            torch.load(settings.MODEL_EFFNET_PATH, map_location=self.device))
        self.efficientnet.eval()
        logger.info("EfficientNet-B3 loaded ✓")

        # Initialize Grad-CAM hooks
        self.gradcam_dn = GradCAM(self.densenet,     self.densenet.features.norm5)
        self.gradcam_en = GradCAM(self.efficientnet, self.efficientnet.backbone.bn2)
        logger.info("Grad-CAM hooks registered ✓")

    def predict(self, tensor: torch.Tensor) -> PredictionResult:
        """
        Run ensemble inference on preprocessed tensor.

        Args:
            tensor: (1, 3, 224, 224) normalized tensor

        Returns:
            PredictionResult dataclass with all prediction info + Grad-CAM maps
        """
        tensor = tensor.to(self.device)

        # ── Inference ──────────────────────────────────────────
        with torch.no_grad():
            probs_dn = F.softmax(
                self.densenet(tensor), dim=1).cpu().numpy()[0]
            probs_en = F.softmax(
                self.efficientnet(tensor), dim=1).cpu().numpy()[0]

        # ── Soft voting ensemble ────────────────────────────────
        probs_ensemble = (probs_dn + probs_en) / 2.0
        pred_idx       = int(probs_ensemble.argmax())
        pred_class     = CLASSES[pred_idx]
        confidence     = float(probs_ensemble[pred_idx])

        dn_idx  = int(probs_dn.argmax())
        en_idx  = int(probs_en.argmax())
        pred_dn = CLASSES[dn_idx]
        pred_en = CLASSES[en_idx]

        # ── Grad-CAM ────────────────────────────────────────────
        cam_dn = self.gradcam_dn.generate(tensor, pred_idx)
        cam_en = self.gradcam_en.generate(tensor, pred_idx)

        return PredictionResult(
            pred_class              = pred_class,
            confidence              = confidence,
            probabilities           = {CLASSES[i]: float(probs_ensemble[i])
                                       for i in range(4)},
            densenet_prediction     = pred_dn,
            densenet_confidence     = float(probs_dn[dn_idx]),
            efficientnet_prediction = pred_en,
            efficientnet_confidence = float(probs_en[en_idx]),
            model_agreement         = pred_dn == pred_en,
            cam_densenet            = cam_dn,
            cam_efficientnet        = cam_en,
        )


# ── Singleton instance (loaded once at startup) ────────────────────────────
_model_instance: EnsembleModel | None = None


def get_model() -> EnsembleModel:
    global _model_instance
    if _model_instance is None:
        _model_instance = EnsembleModel()
    return _model_instance
