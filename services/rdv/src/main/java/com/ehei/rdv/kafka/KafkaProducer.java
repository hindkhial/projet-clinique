package com.ehei.rdv.kafka;

import com.ehei.rdv.Model.Rdv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rdv.events.RdvCreatedEvent;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Rdv rdv) {
        RdvCreatedEvent event = RdvCreatedEvent.newBuilder()
                .setId(rdv.getId().toString())
                .setIdMedecin(rdv.getIdMedecin().toString())
                .setIdPatient(rdv.getIdPatient().toString())
                .setDateRdv(rdv.getDateRdv().toString())
                    .build();

        try {
            kafkaTemplate.send("Rdv_Created", event.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
