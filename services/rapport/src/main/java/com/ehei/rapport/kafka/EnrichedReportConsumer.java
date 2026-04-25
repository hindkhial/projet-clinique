package com.ehei.rapport.kafka;

import orchestrator.events.ReportDataReadyEvent;
import com.ehei.rapport.service.RapportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrichedReportConsumer {

    private final RapportService rapportService;

    @KafkaListener(topics = "Report_Data_Ready", groupId = "rapport-service-group")
    public void consumeEnrichedEvent(byte[] message) {
        try {
            ReportDataReadyEvent event = ReportDataReadyEvent.parseFrom(message);
            log.info("Received enriched event for consultation ID: {}", event.getConsultationId());
            rapportService.createFromEnrichedEvent(event);
        } catch (Exception e) {
            log.error("Failed to process ReportDataReadyEvent: {}", e.getMessage());
        }
    }
}
