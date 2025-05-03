package com.example.notificationservice.kafka;

import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Notification.NotificationRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-requests", groupId = "notification-service")
    public void listen(NotificationRequestDTO request) {
        try {
            log.info("Received notification request: {}", request);
            notificationService.sendNotification(request);
        } catch (Exception e) {
            log.error("Error processing notification: ", e);
        }
    }
}
