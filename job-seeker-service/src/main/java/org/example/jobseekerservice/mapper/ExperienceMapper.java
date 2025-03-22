package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Experience.ExperienceDTO;
import org.example.jobseekerservice.dto.Experience.request.CreateExperienceRequest;
import org.example.jobseekerservice.dto.Experience.request.UpdateExperienceRequest;
import org.example.jobseekerservice.entity.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExperienceMapper extends BaseMapper {
    ExperienceDTO toDto(Experience experience);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "locationType", expression = "java(org.example.jobseekerservice.entity.LocationType.valueOf(request.getLocationType().toUpperCase()))")
    @Mapping(target = "employeeType", expression = "java(org.example.jobseekerservice.entity.EmployeeType.valueOf(request.getEmployeeType().toUpperCase()))")
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Experience toEntity(CreateExperienceRequest request);

    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "locationType", expression = "java(request.getLocationType() != null ? org.example.jobseekerservice.entity.LocationType.valueOf(request.getLocationType().toUpperCase()) : null)")
    @Mapping(target = "employeeType", expression = "java(request.getEmployeeType() != null ? org.example.jobseekerservice.entity.EmployeeType.valueOf(request.getEmployeeType().toUpperCase()) : null)")
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(UpdateExperienceRequest request, @MappingTarget Experience experience);
}
