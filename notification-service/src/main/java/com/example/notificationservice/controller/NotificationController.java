package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationDTO;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUserNotifications(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size
    ) {
        Page<NotificationDTO> pages = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách thông báo thành công"));
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getAllUserNotifications(
            @PathVariable String userId
    ) {
        List<NotificationDTO> notifications = notificationService.getAllUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Lấy tất cả thông báo thành công"));
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Cập nhật thông báo đã đọc thành công", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa thông báo thành công", HttpStatus.OK)
        );
    }
}
