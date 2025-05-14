package com.example.jobservice.mapper;

import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.Job;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.Job.JobStatus;
import org.example.common.dto.Job.JobWithRecruiterDTO;
import org.example.common.dto.Recruiter.RecruiterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {
    JobDTO toDto(Job job);

    Job toEntity(JobDTO jobDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(com.example.jobservice.entity.JobType.valueOf(createJobRequest.getType().toUpperCase()))")
    Job toEntity(CreateJobRequest createJobRequest);

    void updateJobFromRequest(UpdateJobRequest updateJobRequest, @MappingTarget Job job);

    @Mapping(target = "id", source = "jobDTO.id")
    @Mapping(target = "recruiterId", source = "jobDTO.recruiterId")
    @Mapping(target = "recruiterName", source = "recruiterDTO.name")
    @Mapping(target = "recruiterImage", source = "recruiterDTO.image")
    @Mapping(target = "name", source = "jobDTO.name")
    @Mapping(target = "description", source = "jobDTO.description")
    @Mapping(target = "salary", source = "jobDTO.salary")
    @Mapping(target = "requirement", source = "jobDTO.requirement")
    @Mapping(target = "benefit", source = "jobDTO.benefit")
    @Mapping(target = "deadline", source = "jobDTO.deadline")
    @Mapping(target = "slots", source = "jobDTO.slots")
    @Mapping(target = "type", source = "jobDTO.type")
    @Mapping(target = "date", source = "jobDTO.date")
    @Mapping(target = "education", source = "jobDTO.education")
    @Mapping(target = "experience", source = "jobDTO.experience")
    @Mapping(target = "closeWhenFull", source = "jobDTO.closeWhenFull")
    @Mapping(target = "status", source = "jobDTO.status")
    JobWithRecruiterDTO toJobWithRecruiterDTO(JobDTO jobDTO, RecruiterDTO recruiterDTO);
}
