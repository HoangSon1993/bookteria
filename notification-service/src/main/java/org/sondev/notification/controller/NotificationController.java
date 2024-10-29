package org.sondev.notification.controller;

import org.sondev.event.dto.NotificationEvent;
import org.sondev.notification.dto.request.Recipient;
import org.sondev.notification.dto.request.SendEmailRequest;
import org.sondev.notification.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationController {
    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "notification-delivery")
    void listenNotificationDelivery(NotificationEvent event) {
        log.info("Message received: {}", event.toString());

        emailService.sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(event.getRecipient()).build())
                .subject(event.getSubject())
                .htmlContent(event.getBody())
                .build());
    }
}
