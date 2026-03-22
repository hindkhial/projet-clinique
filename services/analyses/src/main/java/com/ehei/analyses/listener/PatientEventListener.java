package com.ehei.analyses.listener;

import com.ehei.analyses.events.PatientCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PatientEventListener {

    @KafkaListener(topics = "patient-events", groupId = "analyses-service")
    public void handlePatientCreated(PatientCreatedEvent event) {
        log.info("  Événement reçu : PatientCreated");
        log.info("   - Patient ID: {}", event.getPatientId());
        log.info("   - Nom: {} {}", event.getNom(), event.getPrenom());
        log.info("   - Email: {}", event.getEmail());
        log.info("   - Event ID: {}", event.getEventId());
        log.info("   - Timestamp: {}", event.getTimestamp());

        // TODO: Vous pouvez ajouter de la logique ici
        // Par exemple : Créer un dossier d'analyses vide pour ce patient
    }
}