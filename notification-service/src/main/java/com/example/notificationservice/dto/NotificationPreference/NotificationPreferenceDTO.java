package com.example.notificationservice.dto.NotificationPreference;

import lombok.Data;
import org.common.dto.Notification.NotificationType;

import java.util.Map;

@Data
public class NotificationPreferenceDTO {
    private String id;
    private String userId;
    private Map<NotificationType, Boolean> enabledNotificationTypes;
}
