package com.ehei.consultation.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationCreateRequest {

    @NotNull(message = "idPatient est obligatoire")
    private Long idPatient;

    @NotNull(message = "idMedecin est obligatoire")
    private Long idMedecin;

    private Long idRdv;
}

