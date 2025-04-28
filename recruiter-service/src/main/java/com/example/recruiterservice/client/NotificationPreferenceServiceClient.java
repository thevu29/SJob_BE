package com.example.recruiterservice.client;

import com.example.recruiterservice.config.FeignClientInterceptor;
import jakarta.validation.Valid;
import org.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.common.dto.NotificationPreference.NotificationPreferenceDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service-preferences", url = "${service.notification.url}", path = "/api/notification-preferences", configuration = FeignClientInterceptor.class)
public interface NotificationPreferenceServiceClient {
    @PostMapping
    ApiResponse<NotificationPreferenceDTO> createNotificationPreference(@Valid @RequestBody NotificationPreferenceCreateDTO createDTO);
}
