package org.example.jobseekerservice.mapper;

import org.common.exception.InvalidDateFormatException;
import org.example.jobseekerservice.entity.JobSeeker;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public interface BaseMapper {
    @Named("jobSeekerIdToJobSeeker")
    default JobSeeker jobSeekerIdToJobSeeker(String jobSeekerId) {
        if (jobSeekerId == null) {
            return null;
        }
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setId(jobSeekerId);
        return jobSeeker;
    }

    @Named("jobSeekerToJobSeekerId")
    default String jobSeekerToJobSeekerId(JobSeeker jobSeeker) {
        if (jobSeeker == null) {
            return null;
        }
        return jobSeeker.getId();
    }


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