package org.example.emailservice.kafka;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.common.dto.Email.EmailMessageDTO;
import org.example.emailservice.services.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {
    private final EmailService emailService;

    @KafkaListener(topics = "send-email", groupId = "email-service")
    public void listen(EmailMessageDTO emailMessage) throws MessagingException {
        log.info("Received message for email: {}", emailMessage.getTo());
        emailService.sendEmail(emailMessage);
    }
}
