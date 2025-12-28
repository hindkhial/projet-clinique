package com.ehei.patient.mapper;

import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.model.Patient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PatientMapper {

    public Patient toModel(PatientCreateRequeste dto) {
        return Patient.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .tel(dto.getTel())
                .dateNaissance(dto.getDateNaissance())
                .build();
    }

    public PatientResponse toResponseDto(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .email(patient.getEmail())
                .tel(patient.getTel())
                .dateNaissance(patient.getDateNaissance())
                .build();
    }

    public List<PatientResponse> toResponseDto(List<Patient> patientListe) {
        List<PatientResponse> dtoList = new ArrayList<>();
        for (Patient patient : patientListe) {
            dtoList.add(toResponseDto(patient));
        }
        return dtoList;
    }
}