package com.ehei.radio.mapper;

import com.ehei.radio.dto.CreateRadioRequest;
import com.ehei.radio.dto.RadioResponse;
import com.ehei.radio.model.Radio;
import org.springframework.stereotype.Component;

@Component
public class RadioMapper {

    public Radio toEntity(CreateRadioRequest request) {
        return Radio.builder()
                .patientId(request.getPatientId())
                .radiologueId(request.getRadiologueId())
                .dateRadio(request.getDateRadio())
                .notes(request.getNotes())
                .build();
    }


    public RadioResponse toResponse(Radio radio) {
        return RadioResponse.builder()
                .id(radio.getId())
                .patientId(radio.getPatientId())
                .patientNom(radio.getPatientNom())
                .patientPrenom(radio.getPatientPrenom())
                .radiologueId(radio.getRadiologueId())
                .dateRadio(radio.getDateRadio())
                .fichierRadioUrl(radio.getFichierRadioUrl())
                .notes(radio.getNotes())
                .createdAt(radio.getCreatedAt())
                .updatedAt(radio.getUpdatedAt())
                .build();
    }
}