package com.ehei.consultation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idPatient;

    private Long idMedecin;

    private Long idRdv;

    private String idRadio;

    private Long idAnalyse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultationType type;

    private String typeMaladie;
    private Double confidence;
    private String riskLevel;


    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(columnDefinition = "TEXT")
    private String traitement;

    private LocalDateTime dateConsultation;
}
