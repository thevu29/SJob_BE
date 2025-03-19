package org.example.jobseekerservice.dto.Education;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationDTO {
    private String id;
    private String school;
    private String major;
    private String degree;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String jobSeekerId;
}
