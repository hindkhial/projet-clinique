package com.ehei.notification.kafka;

import com.ehei.notification.service.EmailService;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import staff.events.StaffCreatedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "Staff_Created", groupId = "notification-service-group")
    public void consumeStaffCreatedEvent(byte[] event) {
        try {
            StaffCreatedEvent staffCreatedEvent = StaffCreatedEvent.parseFrom(event);

            log.info(staffCreatedEvent.toString());
            emailService.sendStaffCreatedEmail(staffCreatedEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error("erreur de deserialisation de l evenement {}",e.getMessage());
        }
    }
}
