package com.ehei.consultation;

import com.ehei.analyses.proto.AnalysesSubmittedEvent;
import com.ehei.consultation.dtos.ConsultationCreateRequest;
import com.ehei.consultation.dtos.ConsultationResponse;
import com.ehei.consultation.dtos.ConsultationUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                .type(ConsultationType.NORMAL)
                .build();
    }

    // RADIO mapping

    public Consultation toModel(radio.events.RadioValidatedEvent event) {
        return Consultation.builder()
                .idPatient(Long.parseLong(event.getPatientId()))
                .idMedecin(Long.parseLong(event.getRadiologueId()))
                .idRadio(event.getRadioId())
                .type(ConsultationType.RADIO)
                .typeMaladie(event.getTypeMaladie())
                .confidence(event.getConfidence())
                .riskLevel(event.getRiskLevel())
                .dateConsultation(LocalDateTime.now())
                .observation(event.getNotesRadiologue() != null && !event.getNotesRadiologue().isEmpty() ? "Notes du radiologue: " + event.getNotesRadiologue() : null)
                .build();
    }

    // ANALYSES mapping

    public Consultation toModel(AnalysesSubmittedEvent event) {
        return Consultation.builder()
                .idPatient(event.getPatientId())
                .idAnalyse(event.getAnalyseId())
                .type(ConsultationType.ANALYSES)
                .dateConsultation(LocalDateTime.now())
                .build();
    }

    // Response mapping

    public ConsultationResponse toResponseDto(Consultation consultation) {
        return ConsultationResponse.builder()
                .id(consultation.getId())
                .idPatient(consultation.getIdPatient())
                .idMedecin(consultation.getIdMedecin())
                .idRdv(consultation.getIdRdv())
                .type(consultation.getType())
                .typeMaladie(consultation.getTypeMaladie())
                .confidence(consultation.getConfidence())
                .riskLevel(consultation.getRiskLevel())
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
        if (dto.getTypeMaladie() != null && !dto.getTypeMaladie().trim().isEmpty()) {
            consultation.setTypeMaladie(dto.getTypeMaladie());
        }
    }
}