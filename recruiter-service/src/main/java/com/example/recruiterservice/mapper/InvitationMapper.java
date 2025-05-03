package com.example.recruiterservice.mapper;

import com.example.recruiterservice.dto.Invitation.InvitationDTO;
import com.example.recruiterservice.dto.Invitation.request.CreateInvitationRequest;
import com.example.recruiterservice.entity.Invitation.Invitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvitationMapper {
    Invitation toEntity(InvitationDTO invitationDTO);
    InvitationDTO toDto(Invitation invitation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Invitation toEntity(CreateInvitationRequest request);
}
