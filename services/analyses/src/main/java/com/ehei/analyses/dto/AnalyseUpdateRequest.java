package com.ehei.analyses.dto;

import com.ehei.analyses.enums.Sexe;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyseUpdateRequest {

    @NotNull(message = "ID du patient obligatoire")
    private Long patientId;

    @NotNull(message = "Age obligatoire")
    private Integer age;

    @NotNull(message = "Sexe obligatoire")
    private Sexe sexe;

    @NotNull(message = "Glycémie à jeun obligatoire")
    private Double glycemieJeun;

    @NotNull(message = "Hémoglobine obligatoire")
    private Double hemoglobine;

    @NotNull(message = "Globules blancs obligatoire")
    private Double globulesBlancs;

    @NotNull(message = "Cholestérol total obligatoire")
    private Double cholesterolTotal;

    @NotNull(message = "Créatinine obligatoire")
    private Double creatinine;

    @NotNull(message = "Tension systolique obligatoire")
    private Double tensionSystolique;

    @NotNull(message = "Tension diastolique obligatoire")
    private Double tensionDiastolique;

    @NotNull(message = "Fréquence cardiaque obligatoire")
    private Double frequenceCardiaque;

    @NotNull(message = "Température obligatoire")
    private Double temperature;

    @NotNull(message = "IMC obligatoire")
    private Double imc;
}