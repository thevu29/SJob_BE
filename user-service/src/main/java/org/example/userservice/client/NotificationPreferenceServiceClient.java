package org.example.userservice.client;

import jakarta.validation.Valid;
import org.example.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.example.common.dto.NotificationPreference.NotificationPreferenceDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.userservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service-preferences", url = "${service.notification.url}", path = "/api/notification-preferences", configuration = FeignClientInterceptor.class)
public interface NotificationPreferenceServiceClient {
    @PostMapping
    ApiResponse<NotificationPreferenceDTO> createNotificationPreference(@Valid @RequestBody NotificationPreferenceCreateDTO createDTO);

    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteNotificationPreference(@PathVariable String userId);
}
