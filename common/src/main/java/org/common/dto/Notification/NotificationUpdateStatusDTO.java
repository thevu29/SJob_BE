package org.common.dto.Notification;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationUpdateStatusDTO {
    private String notificationId;
    private boolean read;
}
