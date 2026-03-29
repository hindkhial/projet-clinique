import cv2
import numpy as np
import torch
import torch.nn.functional as F
from PIL import Image


class GradCAM:
    """
    Grad-CAM implementation for DenseNet-121 and EfficientNet-B3.
    Hooks into target layer to extract gradients and activations.
    """

    def __init__(self, model: torch.nn.Module, target_layer: torch.nn.Module):
        self.model       = model
        self.gradients   = None
        self.activations = None

        target_layer.register_forward_hook(
            lambda m, i, o: setattr(self, 'activations', o.detach()))
        target_layer.register_full_backward_hook(
            lambda m, gi, go: setattr(self, 'gradients', go[0].detach()))

    def generate(self, tensor: torch.Tensor, target_class: int) -> np.ndarray:
        """
        Generate Grad-CAM heatmap for a given class.

        Args:
            tensor       : (1, 3, 224, 224) preprocessed input tensor
            target_class : class index to explain

        Returns:
            cam : (224, 224) numpy array normalized to [0, 1]
        """
        tensor = tensor.clone().requires_grad_(True)
        output = self.model(tensor)
        self.model.zero_grad()
        output[0, target_class].backward()

        # Global average pool gradients
        weights = self.gradients.mean(dim=(2, 3), keepdim=True)

        # Weighted combination of activation maps
        cam = F.relu((weights * self.activations).sum(dim=1, keepdim=True))
        cam = cam.squeeze().cpu().numpy()

        # Normalize to [0, 1]
        if cam.max() > cam.min():
            cam = (cam - cam.min()) / (cam.max() - cam.min())
        else:
            cam = np.zeros_like(cam)

        # Resize to 224x224
        cam = cv2.resize(cam, (224, 224))
        return cam


def generate_overlay(display: Image.Image, cam: np.ndarray, alpha: float = 0.45) -> Image.Image:
    """
    Overlay Grad-CAM heatmap on original image.

    Args:
        display : PIL Image (224x224 RGB)
        cam     : (224, 224) normalized heatmap
        alpha   : heatmap opacity (0=invisible, 1=full heatmap)

    Returns:
        overlaid PIL Image (224x224 RGB)
    """
    heatmap = cv2.applyColorMap(np.uint8(255 * cam), cv2.COLORMAP_JET)
    heatmap = cv2.cvtColor(heatmap, cv2.COLOR_BGR2RGB)
    original = np.array(display)
    overlaid = (alpha * heatmap + (1 - alpha) * original).astype(np.uint8)
    return Image.fromarray(overlaid)


def image_to_bytes(img: Image.Image, fmt: str = "JPEG") -> bytes:
    """Convert PIL Image to bytes for MinIO upload."""
    from io import BytesIO
    buf = BytesIO()
    img.save(buf, format=fmt, quality=90)
    return buf.getvalue()
