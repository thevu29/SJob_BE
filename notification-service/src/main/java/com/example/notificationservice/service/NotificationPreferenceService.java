package com.example.notificationservice.service;

import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.mapper.NotificationPreferenceMapper;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Notification.NotificationType;
import org.example.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.example.common.dto.NotificationPreference.NotificationPreferenceDTO;
import org.example.common.dto.NotificationPreference.NotificationPreferenceUpdateDTO;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
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
                                type -> false
                        ))
        );

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        return notificationPreferenceMapper.toDTO(saved);
    }

    public NotificationPreferenceDTO getNotificationPreference(String userId) {
        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId);
        if (preference == null) {
            throw new ResourceNotFoundException("Không tìm thấy cài đặt thông báo cho người dùng này");
        }
        return notificationPreferenceMapper.toDTO(preference);
    }

    public NotificationPreferenceDTO updateNotificationPreference(String userId, NotificationPreferenceUpdateDTO updateDTO) {
        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId);
        if (preference == null) {
            throw new ResourceNotFoundException("Không tìm thấy cài đặt thông báo cho người dùng này");
        }

        // Update only the specified notification types
        Map<NotificationType, Boolean> currentSettings = preference.getEnabledNotificationTypes();
        updateDTO.getNotificationTypeUpdates().forEach(currentSettings::put);

        preference.setEnabledNotificationTypes(currentSettings);
        NotificationPreference saved = notificationPreferenceRepository.save(preference);

        return notificationPreferenceMapper.toDTO(saved);
    }
}
