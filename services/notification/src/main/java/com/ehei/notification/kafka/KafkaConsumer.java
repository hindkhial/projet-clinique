package com.ehei.notification.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import staff.events.StaffCreatedEvent;

@Slf4j
@Service
public class KafkaConsumer {
    @KafkaListener(topics = "Staff_Created", groupId = "notification-service-group")
    public void consumeStaffCreatedEvent(byte[] event) {
        try {
            StaffCreatedEvent staffCreatedEvent = StaffCreatedEvent.parseFrom(event);

            log.info(staffCreatedEvent.toString());
        } catch (InvalidProtocolBufferException e) {
            log.error("erreur de deserialisation de l evenement {}",e.getMessage());
        }
    }
}
