package com.ehei.consultation;

import com.ehei.consultation.dtos.ConsultationCreateRequest;
import com.ehei.consultation.dtos.ConsultationResponse;
import com.ehei.consultation.dtos.ConsultationUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ConsultationMapper {

    public Consultation toModel(ConsultationCreateRequest dto) {
        return Consultation.builder()
                .idPatient(dto.getIdPatient())
                .idMedecin(dto.getIdMedecin())
                .idRdv(dto.getIdRdv())
                .build();
    }

    public ConsultationResponse toResponseDto(Consultation consultation) {
        return ConsultationResponse.builder()
                .id(consultation.getId())
                .idPatient(consultation.getIdPatient())
                .idMedecin(consultation.getIdMedecin())
                .idRdv(consultation.getIdRdv())
                .observation(consultation.getObservation())
                .traitement(consultation.getTraitement())
                .dateConsultation(consultation.getDateConsultation())
                .build();
    }

    public List<ConsultationResponse> toResponseDto(List<Consultation> consultationList) {
        List<ConsultationResponse> dtoList = new ArrayList<>();
        for (Consultation consultation : consultationList) {
            dtoList.add(toResponseDto(consultation));
        }
        return dtoList;
    }

    public void updateEntity(Consultation consultation, ConsultationUpdateRequest dto) {
        consultation.setObservation(dto.getObservation());
        consultation.setTraitement(dto.getTraitement());
    }
}
