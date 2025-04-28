package com.example.notificationservice.controller;

import com.example.notificationservice.repository.NotificationPreferenceRepository;
import com.example.notificationservice.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.common.dto.NotificationPreference.NotificationPreferenceDTO;
import org.common.dto.NotificationPreference.NotificationPreferenceUpdateDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {
    private final NotificationPreferenceService notificationPreferenceService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> getNotificationPreference(@PathVariable String userId) {
        NotificationPreferenceDTO preference = notificationPreferenceService.getNotificationPreference(userId);
        return ResponseEntity.ok(
                ApiResponse.success(preference, "Lấy cài đặt thông báo thành công", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> createNotificationPreference(@Valid @RequestBody NotificationPreferenceCreateDTO createDTO) {
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceService.createNotificationPreference(createDTO);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(ApiResponse.success(notificationPreferenceDTO, "Tạo cài đặt thông báo thành công", HttpStatus.CREATED));

    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> updateNotificationPreference(
            @PathVariable String userId,
            @Valid @RequestBody NotificationPreferenceUpdateDTO updateDTO) {
        NotificationPreferenceDTO updated = notificationPreferenceService.updateNotificationPreference(userId, updateDTO);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Cập nhật cài đặt thông báo thành công", HttpStatus.OK)
        );
    }
}
