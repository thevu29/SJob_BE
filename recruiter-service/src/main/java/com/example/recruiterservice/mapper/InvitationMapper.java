package com.example.recruiterservice.mapper;

import com.example.recruiterservice.dto.Invitation.InvitationDTO;
import com.example.recruiterservice.dto.Invitation.request.CreateInvitationRequest;
import com.example.recruiterservice.dto.Recruiter.request.CreateRecruiterRequest;
import com.example.recruiterservice.dto.Recruiter.request.UpdateRecruiterRequest;
import com.example.recruiterservice.entity.Invitation.Invitation;
import com.example.recruiterservice.entity.Recruiter;
import org.common.dto.Field.FieldDTO;
import org.common.dto.Recruiter.RecruiterCreationDTO;
import org.common.dto.Recruiter.RecruiterDTO;
import org.common.dto.Recruiter.RecruiterWithUserDTO;
import org.common.dto.User.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
