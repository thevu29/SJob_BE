package org.example.common.dto.Application;

import lombok.Data;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;

@Data
public class ApplicationDTO {
    private String id;
    private String jobId;
    private String jobSeekerId;
    private String resumeId;
    private String resumeUrl;
    private String status;
    private String message;
    private JobDTO job;
    private JobSeekerWithUserDTO jobSeeker;
}
