package com.ehei.consultation.kafka;

import radio.events.RadioValidatedEvent;
import com.ehei.consultation.ConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RadioValidatedKafkaConsumer {

    private final ConsultationService consultationService;

    @KafkaListener(
            topics = "radio.validated",
            groupId = "consultation-service-group"
    )
    public void consume(byte[] message) {
        try {
            RadioValidatedEvent event = RadioValidatedEvent.parseFrom(message);

            log.info("Received RadioValidatedEvent for patient_id: {} radio_id: {}",
                    event.getPatientId(),
                    event.getRadioId());

            consultationService.createFromValidatedRadio(event);

        } catch (Exception e) {
            log.error("Failed to process RadioValidatedEvent: {}", e.getMessage());
        }
    }
}