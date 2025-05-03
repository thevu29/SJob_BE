package org.example.common.dto.Notification;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
    private String userId;

    private String email;

    private NotificationType type;

    private Map<String, Object> metaData;
}
