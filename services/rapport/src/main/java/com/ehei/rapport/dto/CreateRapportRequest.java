package com.ehei.rapport.dto;


import com.ehei.rapport.model.TypeRapport;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRapportRequest {

    @NotNull(message = "Patient ID est obligatoire")
    private Long patientId;

    @NotNull(message = "Medecin ID est obligatoire")
    private Long medecinId;

    @NotNull(message = "Type de rapport est obligatoire")
    private TypeRapport typeRapport;

    @NotNull(message = "Date du rapport est obligatoire")
    private LocalDateTime dateRapport;

    private String observations;
    private String traitement;
    private String diagnostic;
    private Long consultationId;
}
