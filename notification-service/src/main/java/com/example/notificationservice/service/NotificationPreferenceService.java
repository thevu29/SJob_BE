package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationPreference.NotificationPreferenceDTO;
import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.mapper.NotificationPreferenceMapper;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.common.dto.Notification.NotificationType;
import org.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    public NotificationPreferenceDTO createNotificationPreference(NotificationPreferenceCreateDTO createDTO) {

        // Create entity with default settings
        NotificationPreference preference = notificationPreferenceMapper.createDtoToEntity(createDTO);

        // Set all notification types to enabled by default
        preference.setEnabledNotificationTypes(
                Arrays.stream(NotificationType.values())
                        .collect(Collectors.toMap(
                                type -> type,
                                type -> true
                        ))
        );

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        return notificationPreferenceMapper.toDTO(saved);
    }
}
