package com.ehei.orchestrator.kafka;

import consultation.events.ConsultationCreatedEvent;
import orchestrator.events.ReportDataReadyEvent;
import com.ehei.orchestrator.client.MedecinClient;
import com.ehei.orchestrator.client.PatientClient;
import com.ehei.orchestrator.dto.ApiResponse;
import com.ehei.orchestrator.dto.MedecinResponse;
import com.ehei.orchestrator.dto.PatientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsultationCreatedConsumer {

    private final PatientClient patientClient;
    private final MedecinClient medecinClient;
    private final ReportDataReadyProducer producer;

    @Autowired
    public ConsultationCreatedConsumer(PatientClient patientClient, MedecinClient medecinClient, ReportDataReadyProducer producer) {
        this.patientClient = patientClient;
        this.medecinClient = medecinClient;
        this.producer = producer;
    }

    @KafkaListener(topics = "Consultation_Created", groupId = "orchestrator-service-group")
    public void consumeConsultationEvent(byte[] eventBytes) {
        try {
            ConsultationCreatedEvent event = ConsultationCreatedEvent.parseFrom(eventBytes);
            System.out.println("Orchestrator received Consultation_Created for id: " + event.getId());

            // 1. Fetch Patient
            ApiResponse<PatientResponse> patientApiRes = patientClient.getPatientById(event.getIdPatient());
            PatientResponse patient = patientApiRes.getData();
            if (patient == null) {
                throw new RuntimeException("Patient fetch logic returned null data for ID: " + event.getIdPatient());
            }

            // 2. Fetch Medecin (Optional for some types, checking if present)
            MedecinResponse medecin = null;
            if (event.hasIdMedecin()) {
                ApiResponse<MedecinResponse> medApiRes = medecinClient.getMedecinById(event.getIdMedecin());
                medecin = medApiRes.getData();
            }

            // 3. Build Enriched Event
            ReportDataReadyEvent.Builder builder = ReportDataReadyEvent.newBuilder()
                    .setConsultationId(event.getId())
                    .setDateConsultation(event.getDateConsultation())
                    .setObservations(event.getObservation())
                    .setTraitement(event.getTraitement())
                    .setType(event.getType())
                    .setPatientId(patient.getId())
                    .setPatientNom(patient.getNom() != null ? patient.getNom() : "")
                    .setPatientPrenom(patient.getPrenom() != null ? patient.getPrenom() : "")
                    .setPatientEmail(patient.getEmail() != null ? patient.getEmail() : "");

            if (event.hasTypeMaladie()) {
                builder.setTypeMaladie(event.getTypeMaladie());
            }

            if (medecin != null) {
                builder.setMedecinId(medecin.getId())
                        .setMedecinNom(medecin.getNom() != null ? medecin.getNom() : "")
                        .setMedecinPrenom(medecin.getPrenom() != null ? medecin.getPrenom() : "")
                        .setMedecinSpecialite(medecin.getSpecialite() != null ? medecin.getSpecialite() : "");
            }

            // 4. Publish
            producer.sendEnrichedEvent(builder.build());

        } catch (Exception e) {
            System.err.println("Error processing Consultation_Created: " + e.getMessage());
            producer.sendToDLQ(eventBytes, e);
        }
    }
}
