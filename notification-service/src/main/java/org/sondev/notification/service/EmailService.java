package org.sondev.notification.service;

import feign.FeignException;
import org.sondev.notification.dto.request.EmailRequest;
import org.sondev.notification.dto.request.SendEmailRequest;
import org.sondev.notification.dto.request.Sender;
import org.sondev.notification.dto.response.EmailResponse;
import org.sondev.notification.exception.AppException;
import org.sondev.notification.exception.ErrorCode;
import org.sondev.notification.repository.httpclient.EmailClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    private final EmailClient emailClient;

    @Value("${brevo.api-key}")
    private String apiKey;

    public EmailService(EmailClient emailClient) {

        this.emailClient = emailClient;
    }

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("sondev")
                        .email("son.lyhoang2014@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .HtmlContent(request.getHtmlContent())
                .build();

        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
