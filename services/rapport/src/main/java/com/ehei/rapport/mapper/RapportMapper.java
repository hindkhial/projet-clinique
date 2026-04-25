package com.ehei.rapport.mapper;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.model.Rapport;
import consultation.events.ConsultationCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RapportMapper {

    // ── Manual creation (REST) ─────────────────────────────────────────────

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

    // ── From ConsultationCreatedEvent (Kafka) ──────────────────────────────

    public Rapport fromConsultationEvent(ConsultationCreatedEvent event) {
        return Rapport.builder()
                .consultationId(event.getId())
                .patientId(event.getIdPatient())
                .medecinId(event.hasIdMedecin() ? event.getIdMedecin() : null)
                .typeRapport(event.getType())   // NORMAL, RADIO, ANALYSES
                .dateRapport(LocalDateTime.now())
                .observations(event.getObservation().isEmpty() ? null : event.getObservation())
                .traitement(event.getTraitement().isEmpty() ? null : event.getTraitement())
                .build();
    }

    // ── Response mapping ───────────────────────────────────────────────────

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
                .pdfFileUrl(rapport.getPdfFileUrl())
                .createdAt(rapport.getCreatedAt())
                .updatedAt(rapport.getUpdatedAt())
                .build();
    }
}