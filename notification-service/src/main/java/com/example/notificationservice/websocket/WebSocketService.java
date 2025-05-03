package com.example.notificationservice.websocket;

import com.example.notificationservice.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userId, NotificationDTO notificationDTO) {
        try {
            // Gửi thông báo đến topic cụ thể của người dùng
            messagingTemplate.convertAndSend("/topic/notifications/" + userId, notificationDTO);
            log.info("Đã gửi thông báo WebSocket đến người dùng: {}", userId);
        } catch (Exception e) {
            log.error("Lỗi khi gửi thông báo WebSocket: ", e);
            throw e;
        }
    }
}
