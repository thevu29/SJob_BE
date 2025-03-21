package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.dto.Education.EducationDTO;
import org.example.jobseekerservice.dto.Education.request.CreateEducationRequest;
import org.example.jobseekerservice.dto.Education.request.UpdateEducationRequest;
import org.example.jobseekerservice.entity.Education;
import org.example.jobseekerservice.entity.JobSeeker;
import org.example.jobseekerservice.exception.InvalidDateFormatException;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationMapper extends BaseMapper {
    @Mapping(target = "jobSeekerId", source = "jobSeeker", qualifiedByName = "jobSeekerToJobSeekerId")
    EducationDTO toDto(Education education);

    Education toEntity(EducationDTO educationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", source = "jobSeekerId", qualifiedByName = "jobSeekerIdToJobSeeker")
    Education toEntity(CreateEducationRequest request);

    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "endDate", source = "endDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "jobSeeker", ignore = true)
    void toEntity(UpdateEducationRequest request, @MappingTarget Education education);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String dateString) {
        if (dateString == null) {
            return null;
        }

        try {
            YearMonth yearMonth = YearMonth.parse(dateString);
            return yearMonth.atDay(1);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException("Date must be in format yyyy-MM");
        }
    }
}
