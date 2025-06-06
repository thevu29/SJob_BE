package com.example.notificationservice.dto;

import lombok.Builder;
import lombok.Data;
import org.example.common.dto.Notification.NotificationChannel;
import org.example.common.dto.Notification.NotificationType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class NotificationDTO {
    private String id;
    private String userId;
    private NotificationType type;
    private String message;
    private String url;
    private Set<NotificationChannel> channels;
    private boolean read;
    private LocalDateTime createdAt;
}
