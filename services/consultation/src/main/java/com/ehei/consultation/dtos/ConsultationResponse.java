package com.ehei.consultation.dtos;

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

        private String observation;

        private String traitement;

        private LocalDateTime dateConsultation;
    }
