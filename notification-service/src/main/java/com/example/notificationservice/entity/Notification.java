package com.example.notificationservice.entity;

import lombok.*;
import org.common.dto.Notification.NotificationChannel;
import org.common.dto.Notification.NotificationType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private NotificationType type;

    private String title;

    private String content;

    private Set<NotificationChannel> channels;

    private String url;

    @Field(name = "read")
    @Builder.Default
    private boolean read = false;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

}
