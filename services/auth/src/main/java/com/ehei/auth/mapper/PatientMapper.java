package com.ehei.auth.mapper;

import com.ehei.auth.DTOS.patient.PatientCreateRequeste;
import com.ehei.auth.DTOS.patient.PatientRegistrationRequest;
import com.ehei.auth.DTOS.patient.PatientUpdateRequeste;
import com.ehei.auth.DTOS.patient.PatientUpdateRequestAuth;
import org.springframework.stereotype.Service;

@Service
public class PatientMapper {

    public PatientCreateRequeste toPatientCreateRequestDto(PatientRegistrationRequest request) {
        return PatientCreateRequeste.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .tel(request.getTel())
                .dateNaissance(request.getDateNaissance())
                .build();
    }

    public PatientUpdateRequeste toPatientUpdateRequestDto(PatientUpdateRequestAuth request) {
        return PatientUpdateRequeste.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .tel(request.getTel())
                .dateNaissance(request.getDateNaissance())
                .build();
    }
}