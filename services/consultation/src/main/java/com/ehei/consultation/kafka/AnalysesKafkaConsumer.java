package com.ehei.consultation.kafka;

import com.ehei.analyses.proto.AnalysesSubmittedEvent;
import com.ehei.consultation.ConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysesKafkaConsumer {

    private final ConsultationService consultationService;

    @KafkaListener(
            topics = "analyses-events",
            groupId = "consultation-service-group",
            containerFactory = "analysesKafkaListenerContainerFactory"
    )
    public void consume(byte[] message) {
        try {
            AnalysesSubmittedEvent event = AnalysesSubmittedEvent.parseFrom(message);

            log.info("Received AnalysesSubmittedEvent for patient_id: {} analyse_id: {}",
                    event.getPatientId(),
                    event.getAnalyseId());

            consultationService.createFromAnalyses(event);

        } catch (Exception e) {
            log.error("Failed to process AnalysesSubmittedEvent: {}", e.getMessage());
        }
    }
}