package com.example.notificationservice.kafka;

import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.mapper.NotificationMapper;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.NotificationTemplateService;
import com.example.notificationservice.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.common.dto.Email.EmailMessageDTO;
import org.common.dto.Notification.NotificationChannel;
import org.common.dto.Notification.NotificationRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

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
