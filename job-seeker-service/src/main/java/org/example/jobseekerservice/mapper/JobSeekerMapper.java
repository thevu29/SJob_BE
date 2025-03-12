package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.JobSeekerDTO;
import org.example.jobseekerservice.dto.JobSeekerWithUserDTO;
import org.example.jobseekerservice.dto.UserDTO;
import org.example.jobseekerservice.dto.request.CreateJobSeekerRequest;
import org.example.jobseekerservice.entity.JobSeeker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobSeekerMapper {
    JobSeekerDTO toDto(JobSeeker jobSeeker);

    JobSeeker toEntity(JobSeekerDTO jobSeekerDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seeking", constant = "false")
    JobSeeker toEntity(CreateJobSeekerRequest request);

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
