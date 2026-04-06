package com.ehei.radio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RadioValidationRequest {
    @NotNull(message = "La décision est obligatoire (CONFIRME ou MODIFIE)")
    private String decision;

    private String typeMaladie;

    private Double confidence;

    private String riskLevel;

    private String commentaire; // Mapping to notesRadiologue conceptually
}
