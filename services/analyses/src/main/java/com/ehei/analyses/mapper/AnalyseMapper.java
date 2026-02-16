package com.ehei.analyses.mapper;

import com.ehei.analyses.dto.AnalyseCreateRequest;
import com.ehei.analyses.dto.AnalyseResponse;
import com.ehei.analyses.model.Analyse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AnalyseMapper {

    public Analyse toModel(AnalyseCreateRequest dto) {
        return Analyse.builder()
                .patientId(dto.getPatientId())
                .age(dto.getAge())
                .sexe(dto.getSexe())
                .glycemieJeun(dto.getGlycemieJeun())
                .hemoglobine(dto.getHemoglobine())
                .globulesBlancs(dto.getGlobulesBlancs())
                .cholesterolTotal(dto.getCholesterolTotal())
                .creatinine(dto.getCreatinine())
                .tensionSystolique(dto.getTensionSystolique())
                .tensionDiastolique(dto.getTensionDiastolique())
                .frequenceCardiaque(dto.getFrequenceCardiaque())
                .temperature(dto.getTemperature())
                .imc(dto.getImc())
                .build();
    }

    public AnalyseResponse toResponseDto(Analyse analyse) {
        return AnalyseResponse.builder()
                .id(analyse.getId())
                .patientId(analyse.getPatientId())
                .age(analyse.getAge())
                .sexe(analyse.getSexe())
                .glycemieJeun(analyse.getGlycemieJeun())
                .hemoglobine(analyse.getHemoglobine())
                .globulesBlancs(analyse.getGlobulesBlancs())
                .cholesterolTotal(analyse.getCholesterolTotal())
                .creatinine(analyse.getCreatinine())
                .tensionSystolique(analyse.getTensionSystolique())
                .tensionDiastolique(analyse.getTensionDiastolique())
                .frequenceCardiaque(analyse.getFrequenceCardiaque())
                .temperature(analyse.getTemperature())
                .imc(analyse.getImc())
                .dateAnalyse(analyse.getDateAnalyse())
                .build();
    }

    public List<AnalyseResponse> toResponseDto(List<Analyse> analyseListe) {
        List<AnalyseResponse> dtoList = new ArrayList<>();
        for (Analyse analyse : analyseListe) {
            dtoList.add(toResponseDto(analyse));
        }
        return dtoList;
    }
}