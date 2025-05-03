package org.example.common.dto.Application;

import lombok.Data;

@Data
public class ApplicationDTO {
    private String id;
    private String jobId;
    private String jobSeekerId;
    private String resumeId;
    private String resumeUrl;
    private String status;
    private String message;
}
