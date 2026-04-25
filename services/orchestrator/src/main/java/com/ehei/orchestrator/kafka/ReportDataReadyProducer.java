package com.ehei.orchestrator.kafka;

import orchestrator.events.ReportDataReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportDataReadyProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    public ReportDataReadyProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEnrichedEvent(ReportDataReadyEvent event) {
        kafkaTemplate.send("Report_Data_Ready", String.valueOf(event.getConsultationId()), event.toByteArray());
        System.out.println("Published ReportDataReadyEvent for Consultation ID: " + event.getConsultationId());
    }

    public void sendToDLQ(byte[] rawEvent, Exception e) {
        kafkaTemplate.send("Report_Data_Ready_DLQ", rawEvent);
        System.err.println("Sent raw event to DLQ because of: " + e.getMessage());
    }
}
