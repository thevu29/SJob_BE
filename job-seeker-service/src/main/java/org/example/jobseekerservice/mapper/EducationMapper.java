package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Education.EducationDTO;
import org.example.jobseekerservice.dto.Education.request.CreateEducationRequest;
import org.example.jobseekerservice.dto.Education.request.UpdateEducationRequest;
import org.example.jobseekerservice.entity.Education;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationMapper extends BaseMapper {
    @Mapping(target = "jobSeekerId", source = "jobSeeker", qualifiedByName = "jobSeekerToJobSeekerId")
    EducationDTO toDto(Education education);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Education toEntity(CreateEducationRequest request);

    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(UpdateEducationRequest request, @MappingTarget Education education);
}
