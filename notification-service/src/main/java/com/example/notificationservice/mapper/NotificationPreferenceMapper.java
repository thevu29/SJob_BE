package com.example.notificationservice.mapper;

import com.example.notificationservice.entity.NotificationPreference;
import org.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.common.dto.NotificationPreference.NotificationPreferenceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationPreferenceMapper {
    NotificationPreference toEntity(NotificationPreferenceDTO notificationPreferenceDTO);
    NotificationPreferenceDTO toDTO(NotificationPreference notificationPreference);

    @Mapping(target = "id", ignore = true)
    NotificationPreference createDtoToEntity(NotificationPreferenceCreateDTO createDTO);
}
