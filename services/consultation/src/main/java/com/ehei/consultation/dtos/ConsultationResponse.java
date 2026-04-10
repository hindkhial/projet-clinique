package com.ehei.consultation.dtos;

import com.ehei.consultation.ConsultationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class ConsultationResponse {

        private Long id;

        private Long idPatient;

        private Long idMedecin;

        private Long idRdv;
        private ConsultationType type;

        private String typeMaladie;
        private Double confidence;
        private String riskLevel;

        private String observation;

        private String traitement;

        private LocalDateTime dateConsultation;
    }
