package com.ehei.rapport.dto;

import com.ehei.rapport.model.TypeRapport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RapportResponse {
    private Long id;
    private Long patientId;
    private String patientNom;
    private String patientPrenom;
    private String patientEmail;
    private String patientCin;
    private Long medecinId;
    private String medecinNom;
    private String medecinPrenom;
    private String medecinSpecialite;
    private TypeRapport typeRapport;
    private LocalDateTime dateRapport;
    private String observations;
    private String traitement;
    private String diagnostic;
    private Long consultationId;
    private String pdfFileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

