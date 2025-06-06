package com.example.recruiterservice.mapper;

import com.example.recruiterservice.dto.Recruiter.request.CreateRecruiterRequest;
import com.example.recruiterservice.dto.Recruiter.request.UpdateRecruiterRequest;
import com.example.recruiterservice.entity.Recruiter;
import org.example.common.dto.Field.FieldDTO;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.User.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecruiterMapper {
    RecruiterDTO toDto(Recruiter recruiter);

    Recruiter toEntity(RecruiterDTO recruiterDTO);

    @Mapping(target = "fieldId", source = "fieldId")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "status", constant = "false")
    Recruiter toEntity(CreateRecruiterRequest request);

    @Mapping(target = "status", constant = "false")
    Recruiter toEntity(RecruiterCreationDTO request);

    @Mapping(target = "image", ignore = true)
    void toEntity(UpdateRecruiterRequest request, @MappingTarget Recruiter recruiter);

    @Mapping(target = "id", source = "recruiterDTO.id")
    @Mapping(target = "userId", source = "recruiterDTO.userId")
    @Mapping(target = "fieldId", source = "recruiterDTO.fieldId")
    @Mapping(target = "name", source = "recruiterDTO.name")
    @Mapping(target = "about", source = "recruiterDTO.about")
    @Mapping(target = "image", source = "recruiterDTO.image")
    @Mapping(target = "website", source = "recruiterDTO.website")
    @Mapping(target = "address", source = "recruiterDTO.address")
    @Mapping(target = "members", source = "recruiterDTO.members")
    @Mapping(target = "status", source = "recruiterDTO.status")
    @Mapping(target = "email", source = "userDTO.email")
    @Mapping(target = "fieldName", ignore = true)
    @Mapping(target = "role", source = "userDTO.role")
    @Mapping(target = "active", source = "userDTO.active")
    @Mapping(target = "createdAt", source = "userDTO.createdAt")
    @Mapping(target = "updatedAt", source = "userDTO.updatedAt")
    RecruiterWithUserDTO toDto(RecruiterDTO recruiterDTO, UserDTO userDTO);

    @Mapping(target = "id", source = "recruiterDTO.id")
    @Mapping(target = "userId", source = "recruiterDTO.userId")
    @Mapping(target = "fieldId", source = "recruiterDTO.fieldId")
    @Mapping(target = "name", source = "recruiterDTO.name")
    @Mapping(target = "about", source = "recruiterDTO.about")
    @Mapping(target = "image", source = "recruiterDTO.image")
    @Mapping(target = "website", source = "recruiterDTO.website")
    @Mapping(target = "address", source = "recruiterDTO.address")
    @Mapping(target = "members", source = "recruiterDTO.members")
    @Mapping(target = "status", source = "recruiterDTO.status")
    @Mapping(target = "email", source = "userDTO.email")
    @Mapping(target = "fieldName", source = "fieldDTO.name")
    @Mapping(target = "role", source = "userDTO.role")
    @Mapping(target = "active", source = "userDTO.active")
    @Mapping(target = "createdAt", source = "userDTO.createdAt")
    @Mapping(target = "updatedAt", source = "userDTO.updatedAt")
    RecruiterWithUserDTO toDtoWithField(RecruiterDTO recruiterDTO, UserDTO userDTO, FieldDTO fieldDTO);
}
