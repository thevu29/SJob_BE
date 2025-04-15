package com.example.jobservice.mapper;

import com.example.jobservice.dto.Job.JobDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;


import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {
    JobDTO toDto(Job job);

    Job toEntity(JobDTO jobDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(com.example.jobservice.entity.JobType.valueOf(createJobRequest.getType().toUpperCase()))")
    Job toEntity(CreateJobRequest createJobRequest);

    void updateJobFromRequest(UpdateJobRequest updateJobRequest, @MappingTarget Job job);


}
