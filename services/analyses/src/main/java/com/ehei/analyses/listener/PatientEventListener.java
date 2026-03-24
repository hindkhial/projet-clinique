package com.ehei.analyses.listener;

import com.ehei.events.PatientCreatedEvent; // ✅ classe générée par Protobuf
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PatientEventListener {

    @KafkaListener(topics = "patient-events", groupId = "analyses-service")
    public void handlePatientCreated(byte[] message) {
        try {
            // ✅ Désérialiser le byte[] en objet Protobuf
            PatientCreatedEvent event = PatientCreatedEvent.parseFrom(message);

            log.info("✅ Événement reçu : PatientCreated");
            log.info("   - Patient ID: {}", event.getId());
            log.info("   - Nom: {} {}", event.getNom(), event.getPrenom());
            log.info("   - Email: {}", event.getEmail());
            log.info("   - Téléphone: {}", event.getTelephone());
            log.info("   - Date naissance: {}", event.getDateNaissance());

            // TODO: Créer un dossier d'analyses vide pour ce patient

        } catch (InvalidProtocolBufferException e) {
            log.error("❌ Erreur de désérialisation Protobuf: {}", e.getMessage());
        }
    }
}