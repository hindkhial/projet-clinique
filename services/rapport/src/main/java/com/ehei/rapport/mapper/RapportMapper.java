package com.ehei.rapport.mapper;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.model.Rapport;
import orchestrator.events.ReportDataReadyEvent;
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

    // ── From ReportDataReadyEvent (Kafka Orchestrator) ──────────────────────────────

    public Rapport fromEnrichedEvent(ReportDataReadyEvent event) {
        return Rapport.builder()
                .consultationId(event.getConsultationId())
                .patientId(event.getPatientId())
                .patientNom(event.getPatientNom())
                .patientPrenom(event.getPatientPrenom())
                .patientEmail(event.getPatientEmail())
                .typeMaladie(event.hasTypeMaladie() ? event.getTypeMaladie() : null)
                .medecinId(event.hasMedecinId() ? event.getMedecinId() : null)
                .medecinNom(event.getMedecinNom())
                .medecinPrenom(event.getMedecinPrenom())
                .medecinSpecialite(event.getMedecinSpecialite())
                .typeRapport(event.getType())   // NORMAL, RADIO, ANALYSES
                .dateRapport(LocalDateTime.now())
                .observations(event.getObservations().isEmpty() ? null : event.getObservations())
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
                .typeMaladie(rapport.getTypeMaladie())
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