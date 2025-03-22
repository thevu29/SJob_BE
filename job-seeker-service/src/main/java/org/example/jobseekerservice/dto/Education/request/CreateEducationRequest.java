package org.example.jobseekerservice.dto.Education.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateEducationRequest {
    @NotBlank(message = "School is required")
    private String school;

    @NotBlank(message = "Major is required")
    private String major;

    @NotBlank(message = "Degree is required")
    private String degree;

    private String description;

    @NotBlank(message = "Start date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    private String startDate;

    @NotBlank(message = "End date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "End date must be in format yyyy-MM")
    private String endDate;

    @NotBlank(message = "Job seeker is required")
    private String jobSeekerId;
}
