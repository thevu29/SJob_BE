package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationDTO;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.mapper.NotificationMapper;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Email.EmailMessageDTO;
import org.example.common.dto.Notification.NotificationChannel;
import org.example.common.dto.Notification.NotificationRequestDTO;
import org.example.common.dto.Notification.NotificationType;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final KafkaTemplate<String, EmailMessageDTO> kafkaTemplate;
    private final WebSocketService webSocketService;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationTemplateService notificationTemplateService;

    public List<NotificationDTO> getAllUserNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public void sendNotification(NotificationRequestDTO request) {
        NotificationPreference preference = preferenceRepository.findByUserId(request.getUserId());

        try {
            String message = notificationTemplateService.renderTitle(request.getType(), request.getMetaData());

            Set<NotificationChannel> channels = determineEnabledChannels(preference, request);
            String url = generateUrlFromType(request.getType(), request.getMetaData());

            Notification notification = notificationMapper.notificationRequestToEntity(request, message, channels, url);

            Notification savedNotification = notificationRepository.save(notification);

            dispatchNotifications(savedNotification, request);
        } catch (Exception e) {
            log.error("Error sending notification: ", e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    private Set<NotificationChannel> determineEnabledChannels(NotificationPreference preference, NotificationRequestDTO request) {
        Set<NotificationChannel> channels = new HashSet<>();
        channels.add(NotificationChannel.IN_APP);

        if (preference.getEnabledNotificationTypes().get(request.getType())) {
            channels.add(NotificationChannel.EMAIL);
        }

        return channels;
    }

    private void dispatchNotifications(Notification notification, NotificationRequestDTO request) {
        try {
            if (notification.getChannels().contains(NotificationChannel.EMAIL)) {
                String title = notificationTemplateService.renderTitle(request.getType(), request.getMetaData());
                String content = notificationTemplateService.renderContent(request.getType(), request.getMetaData());

                EmailMessageDTO emailMessage = EmailMessageDTO.builder()
                        .to(request.getEmail())
                        .subject(title)
                        .body(content)
                        .fileUrl((String) request.getMetaData().get("fileUrl"))
                        .build();

                kafkaTemplate.send("send-email", emailMessage);
            }

            if (notification.getChannels().contains(NotificationChannel.IN_APP)) {
                webSocketService.sendNotification(
                        notification.getUserId(),
                        notificationMapper.toDTO(notification)
                );
            }
        } catch (Exception e) {
            log.error("Error dispatching notifications: ", e);
            throw new RuntimeException("Failed to dispatch notifications", e);
        }
    }

    private String generateUrlFromType(NotificationType type, Map<String, Object> metaData) {
        return switch (type) {
            case JOB_INVITATION -> "/invitations/" + metaData.get("invitationId");
            case JOB_EXPIRY, JOB_RECOMMENDATION -> "/jobs/" + metaData.get("jobId");
//            case JOB_APPLICATION -> "/applications/" + metaData.get("applicationId");
            default -> "";
        };
    }

    public Page<NotificationDTO> getUserNotifications(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Notification> notifications = notificationRepository.findByUserId(
                userId, pageable);
        return notifications.map(notificationMapper::toDTO);
    }

    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));

        notificationRepository.delete(notification);
    }

    @Scheduled(cron = "0 0 0 * * *") // Runs at midnight every day
    public void deleteOldNotifications() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        try {
            List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(thirtyDaysAgo);
            notificationRepository.deleteAll(oldNotifications);

            log.info("Deleted {} notifications older than 30 days", oldNotifications.size());
        } catch (Exception e) {
            log.error("Error deleting old notifications: ", e);
        }
    }
}
