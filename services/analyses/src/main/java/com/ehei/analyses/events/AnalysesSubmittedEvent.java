package com.ehei.analyses.events;

import com.ehei.analyses.enums.Sexe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysesSubmittedEvent {

    // Métadonnées de l'événement
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    // Données de l'analyse
    private Long analyseId;
    private Long patientId;

    // Données démographiques
    private Integer age;
    private Sexe sexe;

    // Analyses sanguines
    private Double glycemieJeun;
    private Double hemoglobine;
    private Double globulesBlancs;
    private Double cholesterolTotal;
    private Double creatinine;

    // Signes vitaux
    private Double tensionSystolique;
    private Double tensionDiastolique;
    private Double frequenceCardiaque;
    private Double temperature;
    private Double imc;

    // Date de l'analyse
    private LocalDateTime dateAnalyse;

    // Constructeur utilitaire
    public static AnalysesSubmittedEvent from(
            Long analyseId,
            Long patientId,
            Integer age,
            Sexe sexe,
            Double glycemieJeun,
            Double hemoglobine,
            Double globulesBlancs,
            Double cholesterolTotal,
            Double creatinine,
            Double tensionSystolique,
            Double tensionDiastolique,
            Double frequenceCardiaque,
            Double temperature,
            Double imc,
            LocalDateTime dateAnalyse
    ) {
        return AnalysesSubmittedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("AnalysesSubmitted")
                .timestamp(LocalDateTime.now())
                .analyseId(analyseId)
                .patientId(patientId)
                .age(age)
                .sexe(sexe)
                .glycemieJeun(glycemieJeun)
                .hemoglobine(hemoglobine)
                .globulesBlancs(globulesBlancs)
                .cholesterolTotal(cholesterolTotal)
                .creatinine(creatinine)
                .tensionSystolique(tensionSystolique)
                .tensionDiastolique(tensionDiastolique)
                .frequenceCardiaque(frequenceCardiaque)
                .temperature(temperature)
                .imc(imc)
                .dateAnalyse(dateAnalyse)
                .build();
    }
}