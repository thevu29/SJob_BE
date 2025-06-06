package org.example.common.dto.NotificationPreference;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.common.dto.Notification.NotificationType;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceUpdateDTO {
    private Map<NotificationType, Boolean> notificationTypeUpdates;}
