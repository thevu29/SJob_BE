package org.common.dto.NotificationPreference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.common.dto.Notification.NotificationType;

import java.util.Map;

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
