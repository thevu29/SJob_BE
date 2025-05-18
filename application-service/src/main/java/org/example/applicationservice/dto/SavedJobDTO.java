package org.example.applicationservice.dto;

import lombok.Data;
import org.example.common.dto.Job.JobDTO;

@Data
public class SavedJobDTO {
    private String id;
    private String jobSeekerId;
    private JobDTO job;
}
