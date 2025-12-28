package com.ehei.rapport.mapper;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.model.Rapport;
import org.springframework.stereotype.Component;

@Component
public class RapportMapper {

    //convertir CreateRapportRequest (DTO) to Rapport (Entity)
    public Rapport toEntity(CreateRapportRequest request) {
        return Rapport.builder()
                .patientId(request.getPatientId())
                .medecinId(request.getMedecinId())
                .typeRapport(request.getTypeRapport())
                .dateRapport(request.getDateRapport())
                .observations(request.getObservations())
                .traitement(request.getTraitement())
                .diagnostic(request.getDiagnostic())
                .consultationId(request.getConsultationId())
                .build();
    }

    //convertir Rapport (Entity) to RapportResponse (DTO)
    public RapportResponse toResponse(Rapport rapport) {
        return RapportResponse.builder()
                .id(rapport.getId())
                .patientId(rapport.getPatientId())
                .patientNom(rapport.getPatientNom())
                .patientPrenom(rapport.getPatientPrenom())
                .patientEmail(rapport.getPatientEmail())
                .patientCin(rapport.getPatientCin())
                .medecinId(rapport.getMedecinId())
                .medecinNom(rapport.getMedecinNom())
                .medecinPrenom(rapport.getMedecinPrenom())
                .medecinSpecialite(rapport.getMedecinSpecialite())
                .typeRapport(rapport.getTypeRapport())
                .dateRapport(rapport.getDateRapport())
                .observations(rapport.getObservations())
                .traitement(rapport.getTraitement())
                .diagnostic(rapport.getDiagnostic())
                .consultationId(rapport.getConsultationId())
                .createdAt(rapport.getCreatedAt())
                .updatedAt(rapport.getUpdatedAt())
                .build();
    }


}
