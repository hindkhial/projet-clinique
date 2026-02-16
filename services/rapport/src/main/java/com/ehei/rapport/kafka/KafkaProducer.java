package com.ehei.rapport.kafka;

import com.ehei.rapport.model.Rapport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rapport.events.RapportGeneratedEvent;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRapportGeneratedEvent(Rapport rapport) {
        RapportGeneratedEvent event = RapportGeneratedEvent.newBuilder()
                .setRapportId(rapport.getId().toString())
                .setPatientId(rapport.getPatientId().toString())
                .setMedecinId(rapport.getMedecinId().toString())
                .setTypeRapport(rapport.getTypeRapport().toString())
                .setPdfFileUrl(rapport.getPdfFileUrl() != null ? rapport.getPdfFileUrl() : "")
                .setDateRapport(rapport.getDateRapport().toString())
                .build();

        try {
            kafkaTemplate.send("Rapport_Generated", event.toByteArray());
            log.info("Evenement Rapport_Generated envoye pour rapport ID: {}", rapport.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'evenement Kafka: {}", e.getMessage());
        }
    }
}