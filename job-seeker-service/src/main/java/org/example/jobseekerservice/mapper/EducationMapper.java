package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Education.EducationCreationDTO;
import org.example.jobseekerservice.dto.Education.EducationDTO;
import org.example.jobseekerservice.dto.Education.EducationUpdateDTO;
import org.example.jobseekerservice.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationMapper extends BaseMapper {
    @Mapping(target = "jobSeekerId", source = "jobSeeker", qualifiedByName = "jobSeekerToJobSeekerId")
    EducationDTO toDto(Education education);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Education toEntity(EducationCreationDTO request);

    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(EducationUpdateDTO request, @MappingTarget Education education);
}
