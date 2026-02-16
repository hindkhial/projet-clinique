package com.ehei.analyses.model;

import com.ehei.analyses.enums.Sexe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "analyse_medicale")
public class Analyse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence au patient
    @Column(nullable = false)
    private Long patientId;

    // Données démographiques
    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexe sexe;

    // Analyses sanguines
    @Column(nullable = false)
    private Double glycemieJeun;

    @Column(nullable = false)
    private Double hemoglobine;

    @Column(nullable = false)
    private Double globulesBlancs;

    @Column(nullable = false)
    private Double cholesterolTotal;

    @Column(nullable = false)
    private Double creatinine;

    // Signes vitaux
    @Column(nullable = false)
    private Double tensionSystolique;

    @Column(nullable = false)
    private Double tensionDiastolique;

    @Column(nullable = false)
    private Double frequenceCardiaque;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double imc;

    // Métadonnées
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateAnalyse;
}