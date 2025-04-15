package org.example.userservice.mapper;

import org.common.dto.User.UserCreationDTO;
import org.common.dto.User.UserDTO;
import org.example.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDTO toDto(User user);

    @Mapping(target = "role", expression = "java(org.common.enums.UserRole.valueOf(request.getRole().toUpperCase()))")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreationDTO request);
}
