package org.example.userservice.mapper;

import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    @Mapping(target = "role", expression = "java(org.example.userservice.entity.UserRole.valueOf(createUserRequest.getRole().toUpperCase()))")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);
}
