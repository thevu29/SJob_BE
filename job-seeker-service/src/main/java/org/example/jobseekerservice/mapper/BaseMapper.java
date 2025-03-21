package org.example.jobseekerservice.mapper;

import org.example.jobseekerservice.entity.JobSeeker;
import org.mapstruct.Named;

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
}