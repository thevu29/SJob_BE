package org.example.jobseekerservice.mapper;

import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.User.UserDTO;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerDTO;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerUpdateDTO;
import org.example.jobseekerservice.entity.JobSeeker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobSeekerMapper {
    JobSeekerDTO toDto(JobSeeker jobSeeker);

    JobSeeker toEntity(JobSeekerDTO jobSeekerDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seeking", constant = "false")
    JobSeeker toEntity(JobSeekerCreationDTO request);

    void toEntity(JobSeekerUpdateDTO request, @MappingTarget JobSeeker jobSeeker);

    @Mapping(target = "id", source = "jobSeekerDTO.id")
    @Mapping(target = "userId", source = "jobSeekerDTO.userId")
    @Mapping(target = "name", source = "jobSeekerDTO.name")
    @Mapping(target = "phone", source = "jobSeekerDTO.phone")
    @Mapping(target = "image", source = "jobSeekerDTO.image")
    @Mapping(target = "gender", source = "jobSeekerDTO.gender")
    @Mapping(target = "about", source = "jobSeekerDTO.about")
    @Mapping(target = "address", source = "jobSeekerDTO.address")
    @Mapping(target = "seeking", source = "jobSeekerDTO.seeking")
    @Mapping(target = "email", source = "userDTO.email")
    @Mapping(target = "role", source = "userDTO.role")
    @Mapping(target = "active", source = "userDTO.active")
    @Mapping(target = "createdAt", source = "userDTO.createdAt")
    @Mapping(target = "updatedAt", source = "userDTO.updatedAt")
    JobSeekerWithUserDTO toDto(JobSeekerDTO jobSeekerDTO, UserDTO userDTO);
}
