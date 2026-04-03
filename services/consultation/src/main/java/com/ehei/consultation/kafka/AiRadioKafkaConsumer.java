package com.ehei.consultation.kafka;

import com.ehei.ai.proto.AIPredictionEvent;
import com.ehei.consultation.ConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRadioKafkaConsumer {

    private final ConsultationService consultationService;

    @KafkaListener(
            topics = "ai.prediction.done",
            groupId = "consultation-service-group",
            containerFactory = "aiPredictionKafkaListenerContainerFactory"
    )
    public void consume(byte[] message) {
        try {
            AIPredictionEvent event = AIPredictionEvent.parseFrom(message);

            log.info("Received AIPredictionEvent for patient_id: {} radio_id: {}",
                    event.getPatientId(),
                    event.getRadioId());

            consultationService.createFromAiPrediction(event);

        } catch (Exception e) {
            log.error("Failed to process AIPredictionEvent: {}", e.getMessage());
        }
    }
}