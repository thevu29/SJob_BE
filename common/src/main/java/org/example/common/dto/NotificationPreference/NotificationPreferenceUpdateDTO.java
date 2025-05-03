package org.example.common.dto.NotificationPreference;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.common.dto.Notification.NotificationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceUpdateDTO {
    @NotNull(message = "Notification type không được để trống")
    private NotificationType notificationType;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean enabled;}
