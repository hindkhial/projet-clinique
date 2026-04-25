package com.ehei.rapport.dto;

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
    private Long medecinId;
    private String medecinNom;
    private String medecinPrenom;
    private String medecinSpecialite;
    private String typeRapport;
    private LocalDateTime dateRapport;
    private String typeMaladie;
    private String observations;
    private String traitement;
    private String diagnostic;
    private Long consultationId;
    private String pdfFileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}