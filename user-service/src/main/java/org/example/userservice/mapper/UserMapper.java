package org.example.userservice.mapper;

import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.UserCreationRequest;
import org.example.userservice.dto.request.UserUpdateRequest;
import org.example.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    User toEntity(UserCreationRequest userCreationRequest);

    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
