
import struct
import json


# ── VARINT helpers ─────────────────────────────────────────────────────────
def parse_varint(data: bytes, pos: int) -> tuple[int, int]:
    result, shift = 0, 0
    while True:
        b       = data[pos]; pos += 1
        result |= (b & 0x7F) << shift
        if not (b & 0x80):
            break
        shift += 7
    return result, pos


def encode_varint(value: int) -> bytes:
    bits = []
    while value > 0x7F:
        bits.append((value & 0x7F) | 0x80)
        value >>= 7
    bits.append(value)
    return bytes(bits)


# ── Field encoders ─────────────────────────────────────────────────────────
def encode_string(field_number: int, value: str) -> bytes:
    """Wire type 2 — length delimited."""
    if not value:
        return b""
    encoded = value.encode("utf-8")
    tag     = encode_varint((field_number << 3) | 2)
    length  = encode_varint(len(encoded))
    return tag + length + encoded


def encode_double(field_number: int, value: float) -> bytes:
    """Wire type 1 — 64-bit little-endian double."""
    if value == 0.0:
        return b""
    tag = encode_varint((field_number << 3) | 1)
    return tag + struct.pack("<d", value)


def encode_bool(field_number: int, value: bool) -> bytes:
    """Wire type 0 — varint (0 or 1)."""
    tag = encode_varint((field_number << 3) | 0)
    return tag + encode_varint(1 if value else 0)


# ── DESERIALIZE RadioUploadedEvent ─────────────────────────────────────────
def parse_radio_uploaded_event(data: bytes) -> dict:
    """
    Parse RadioUploadedEvent protobuf bytes into a plain dict.
    """
    FIELD_MAP = {
        1: "radio_id",
        2: "patient_id",
        3: "radiologue_id",
        4: "fichier_radio_url",
        5: "date_radio",
        6: "notes",
    }
    result = {v: "" for v in FIELD_MAP.values()}
    pos    = 0

    while pos < len(data):
        tag, pos     = parse_varint(data, pos)
        field_number = tag >> 3
        wire_type    = tag & 0x7

        if wire_type == 2:
            length, pos = parse_varint(data, pos)
            value        = data[pos:pos + length]
            pos         += length
            if field_number in FIELD_MAP:
                result[FIELD_MAP[field_number]] = value.decode("utf-8", errors="replace")
        elif wire_type == 0:
            _, pos = parse_varint(data, pos)
        elif wire_type == 1:
            pos += 8
        elif wire_type == 5:
            pos += 4
        else:
            break

    return result


# ── SERIALIZE AIPredictionEvent ────────────────────────────────────────────
def serialize_ai_prediction_event(
    radio_id:                str,
    patient_id:              str,
    radiologue_id:           str,
    fichier_radio_url:       str,
    heatmap_url:             str,
    type_maladie:            str,
    confidence:              float,
    risk_level:              str,
    model_agreement:         bool,
    date_radio:              str,
    densenet_prediction:     str,
    densenet_confidence:     float,
    efficientnet_prediction: str,
    efficientnet_confidence: float,
    probabilities:           dict,
) -> bytes:
    """
    Serialize AIPredictionEvent to Protobuf bytes.
    Matches ai_prediction.proto field numbers exactly.
    """
    return (
        encode_string(1,  radio_id)                        +
        encode_string(2,  patient_id)                      +
        encode_string(3,  radiologue_id)                   +
        encode_string(4,  fichier_radio_url)               +
        encode_string(5,  heatmap_url)                     +
        encode_string(6,  type_maladie)                    +
        encode_double(7,  confidence)                      +
        encode_string(8,  risk_level)                      +
        encode_bool  (9,  model_agreement)                 +
        encode_string(10, date_radio)                      +
        encode_string(11, densenet_prediction)             +
        encode_double(12, densenet_confidence)             +
        encode_string(13, efficientnet_prediction)         +
        encode_double(14, efficientnet_confidence)         +
        encode_string(15, json.dumps(probabilities))       # dict → JSON string
    )
