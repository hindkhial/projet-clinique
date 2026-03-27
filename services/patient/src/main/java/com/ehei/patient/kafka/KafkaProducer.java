package com.ehei.patient.kafka;
import com.ehei.events.PatientCreatedEvent;
import com.ehei.patient.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPatientCreatedEvent(Patient patient) {
        PatientCreatedEvent event = PatientCreatedEvent.newBuilder()
                .setId(patient.getId().toString())
                .setNom(patient.getNom())
                .setPrenom(patient.getPrenom())
                .setEmail(patient.getEmail())
                .setTelephone(patient.getTel())
                .setDateNaissance(patient.getDateNaissance().toString())
                .build();
        try {
            kafkaTemplate.send("patient-events", event.toByteArray());
            log.info("✅ Event PatientCreated envoyé pour le patient ID: {}", patient.getId());
        } catch (Exception e) {
            log.error("❌ Erreur envoi event Kafka: {}", e.getMessage());
        }
    }
}