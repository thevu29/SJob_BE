package com.example.notificationservice.dto;

import lombok.Builder;
import lombok.Data;
import org.common.dto.Notification.NotificationChannel;
import org.common.dto.Notification.NotificationType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class NotificationDTO {
    private String id;
    private String userId;
    private NotificationType type;
    private String title;
    private String content;
    private Set<NotificationChannel> channels;
    private boolean read;
    private LocalDateTime createdAt;
}
