package com.ehei.analyses.dto;

import com.ehei.analyses.enums.Sexe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyseResponse {

    private Long id;
    private Long patientId;
    private Integer age;
    private Sexe sexe;
    private Double glycemieJeun;
    private Double hemoglobine;
    private Double globulesBlancs;
    private Double cholesterolTotal;
    private Double creatinine;
    private Double tensionSystolique;
    private Double tensionDiastolique;
    private Double frequenceCardiaque;
    private Double temperature;
    private Double imc;
    private LocalDateTime dateAnalyse;
}