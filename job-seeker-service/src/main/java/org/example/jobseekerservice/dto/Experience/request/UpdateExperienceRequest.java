package org.example.jobseekerservice.dto.Experience.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateExperienceRequest {
    private String company;
    private String position;
    private String location;

    private String locationType;
    private String description;
    private String employeeType;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    private String startDate;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    private String endDate;
}
