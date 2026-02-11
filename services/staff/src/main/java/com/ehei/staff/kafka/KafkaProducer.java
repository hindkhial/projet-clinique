package com.ehei.staff.kafka;

import com.ehei.staff.models.Staff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import staff.events.StaffCreatedEvent;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Staff staff) {
        StaffCreatedEvent event = StaffCreatedEvent.newBuilder()
                .setStaffId(staff.getId().toString())
                .setNom(staff.getNom())
                .setPrenom(staff.getPrenom())
                .setEmail(staff.getEmail())
                .setRole(staff.getRole().toString())
                    .build();

        try {
            kafkaTemplate.send("Staff_Created", event.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
