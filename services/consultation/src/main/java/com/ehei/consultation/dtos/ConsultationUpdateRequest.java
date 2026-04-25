package com.ehei.consultation.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationUpdateRequest {

    @NotBlank(message = "observation est obligatoire")
    private String observation;

    @NotBlank(message = "traitement est obligatoire")
    private String traitement;

    private String typeMaladie; // Optional, can be null if patient is "sain"
}
