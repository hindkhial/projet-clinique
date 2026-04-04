package com.ehei.rapport.kafka;

import com.ehei.rapport.service.RapportService;
import consultation.events.ConsultationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationKafkaConsumer {

    private final RapportService rapportService;

    @KafkaListener(
            topics = "Consultation_Created",
            groupId = "rapport-service-group"
    )
    public void consume(byte[] message) {
        try {
            ConsultationCreatedEvent event = ConsultationCreatedEvent.parseFrom(message);

            log.info("Received ConsultationCreatedEvent id: {} type: {}",
                    event.getId(),
                    event.getType());

            rapportService.createFromConsultationEvent(event);

        } catch (Exception e) {
            log.error("Failed to process ConsultationCreatedEvent: {}", e.getMessage());
        }
    }
}