import cv2
import numpy as np
import torch
import torchvision.transforms as transforms
from PIL import Image
from io import BytesIO


# ImageNet normalization (same as training)
MEAN = [0.485, 0.456, 0.406]
STD  = [0.229, 0.224, 0.225]
IMG_SIZE = 224


def preprocess_image(image_bytes: bytes) -> tuple[torch.Tensor, Image.Image]:
    """
    Full preprocessing pipeline — identical to training pipeline.

    Steps:
        1. Decode image bytes → grayscale numpy array
        2. Apply CLAHE (clip=2.0, grid=8x8)
        3. Convert grayscale → 3-channel RGB
        4. Resize to 224x224
        5. Normalize with ImageNet mean/std

    Args:
        image_bytes: raw bytes from MinIO

    Returns:
        tensor  : (1, 3, 224, 224) normalized tensor ready for model
        display : PIL Image (224x224 RGB) for Grad-CAM overlay
    """
    # Decode bytes → numpy
    nparr = np.frombuffer(image_bytes, np.uint8)
    img   = cv2.imdecode(nparr, cv2.IMREAD_GRAYSCALE)

    if img is None:
        raise ValueError("Could not decode image — unsupported format or corrupted file")

    # CLAHE — same params as training
    clahe    = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
    enhanced = clahe.apply(img)

    # Grayscale → 3-channel RGB
    rgb = cv2.cvtColor(enhanced, cv2.COLOR_GRAY2RGB)

    # PIL conversion for torchvision transforms
    pil_img = Image.fromarray(rgb)
    display = pil_img.resize((IMG_SIZE, IMG_SIZE))

    # Normalize
    transform = transforms.Compose([
        transforms.Resize((IMG_SIZE, IMG_SIZE)),
        transforms.ToTensor(),
        transforms.Normalize(mean=MEAN, std=STD),
    ])

    tensor = transform(pil_img).unsqueeze(0)  # (1, 3, 224, 224)
    return tensor, display
