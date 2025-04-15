package org.example.jobseekerservice.dto.Experience;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ExperienceCreationDTO {
    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Position is required")
    private String position;

    private String location;

    @NotBlank(message = "Location type is required")
    private String locationType;

    private String description;

    @NotBlank(message = "Employee type is required")
    private String employeeType;

    @NotBlank(message = "Start date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    private String startDate;

    @NotBlank(message = "End date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    private String endDate;

    @NotBlank(message = "Job seeker ID is required")
    private String jobSeekerId;
}
