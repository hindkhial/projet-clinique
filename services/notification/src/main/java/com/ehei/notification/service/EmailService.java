package com.ehei.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import staff.events.StaffCreatedEvent;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendStaffCreatedEmail(StaffCreatedEvent staffCreatedEvent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(staffCreatedEvent.getEmail());
        message.setSubject("staff created");
        message.setText("new staff has been created with info: " + staffCreatedEvent.toString());
        message.setFrom("noreply@example.com");

        mailSender.send(message);
    }
}
