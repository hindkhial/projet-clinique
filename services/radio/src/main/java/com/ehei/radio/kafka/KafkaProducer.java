package com.ehei.radio.kafka;

import com.ehei.radio.model.Radio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import radio.events.RadioUploadedEvent;
import radio.events.RadioValidatedEvent;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRadioUploadedEvent(Radio radio) {
        RadioUploadedEvent event = RadioUploadedEvent.newBuilder()
                .setRadioId(radio.getId().toString())
                .setPatientId(radio.getPatientId().toString())
                .setRadiologueId(radio.getRadiologueId().toString())
                .setFichierRadioUrl(radio.getFichierRadioUrl())
                .setDateRadio(radio.getDateRadio().toString())
                .setNotes(radio.getNotes() != null ? radio.getNotes() : "")
                .build();

        try {
            kafkaTemplate.send("Radio_Uploaded", event.toByteArray());
            log.info("Evenement Radio_Uploaded envoye pour radio ID: {}", radio.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'evenement Kafka: {}", e.getMessage());
        }
    }

    public void sendRadioValidatedEvent(Radio radio) {
        radio.events.RadioValidatedEvent event = RadioValidatedEvent.newBuilder()
                .setRadioId(radio.getId().toString())
                .setPatientId(radio.getPatientId().toString())
                .setRadiologueId(radio.getRadiologueId().toString())
                .setFichierRadioUrl(radio.getFichierRadioUrl() != null ? radio.getFichierRadioUrl() : "")
                .setTypeMaladie(radio.getTypeMaladie() != null ? radio.getTypeMaladie() : "Non spécifié")
                .setConfidence(radio.getConfidence() != null ? radio.getConfidence() : 0.0)
                .setRiskLevel(radio.getRiskLevel() != null ? radio.getRiskLevel() : "N/A")
                .setHeatmapUrl(radio.getHeatmapUrl() != null ? radio.getHeatmapUrl() : "")
                .setDateValidation(radio.getUpdatedAt() != null ? radio.getUpdatedAt().toString() : "")
                .setNotesRadiologue(radio.getNotesRadiologue() != null ? radio.getNotesRadiologue() : "")
                .build();

        try {
            kafkaTemplate.send("radio.validated", event.toByteArray());
            log.info("Evenement radio.validated envoye pour radio ID: {}", radio.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'evenement radio.validated: {}", e.getMessage());
        }
    }
}