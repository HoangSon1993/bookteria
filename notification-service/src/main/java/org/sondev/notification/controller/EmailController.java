package org.sondev.notification.controller;

import lombok.extern.slf4j.Slf4j;
import org.sondev.notification.dto.request.SendEmailRequest;
import org.sondev.notification.dto.response.ApiResponse;
import org.sondev.notification.dto.response.EmailResponse;
import org.sondev.notification.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request) {
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendEmail(request))
                .build();
    }

    @KafkaListener(topics = "onboard-successful")
    public void listen(String message) {
        log.info("Message received: {}", message);
    }
}
