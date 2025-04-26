package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationPreference.NotificationPreferenceDTO;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import com.example.notificationservice.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {
    private final NotificationPreferenceService notificationPreferenceService;

    @GetMapping
    public String getNotificationPreferences() {
        return "List of notification preferences";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> createNotificationPreference(@Valid @RequestBody NotificationPreferenceCreateDTO createDTO) {
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceService.createNotificationPreference(createDTO);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(ApiResponse.success(notificationPreferenceDTO, "Tạo cài đặt thông báo thành công", HttpStatus.CREATED));

    }
}
