package com.example.notificationservice.mapper;

import com.example.notificationservice.dto.NotificationDTO;
import com.example.notificationservice.entity.Notification;
import org.common.dto.Notification.NotificationChannel;
import org.common.dto.Notification.NotificationRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
    NotificationDTO toDTO(Notification notification);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "type", source = "request.type")
//    @Mapping(target = "metaData", source = "request.metaData")
    @Mapping(target = "channels", source = "channels")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    Notification notificationRequestToEntity(NotificationRequestDTO request, String title, String content, Set<NotificationChannel> channels);
}
