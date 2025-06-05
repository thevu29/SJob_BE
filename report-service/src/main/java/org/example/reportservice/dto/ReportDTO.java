package org.example.reportservice.dto;

import lombok.Data;
import org.example.reportservice.entity.ReportStatus;

@Data
public class ReportDTO {
    private String id;
    private String jobSeekerId;
    private String jobSeekerEmail;
    private String recruiterId;
    private String recruiterEmail;
    private String message;
    private String reason;
    private ReportStatus status;
    private String evidence;
}
