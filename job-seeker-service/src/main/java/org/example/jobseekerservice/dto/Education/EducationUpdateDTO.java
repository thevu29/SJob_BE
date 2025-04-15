package org.example.jobseekerservice.dto.Education;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EducationUpdateDTO {
    private String school;
    private String major;
    private String degree;
    private String description;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    private String startDate;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "End date must be in format yyyy-MM")
    private String endDate;
}
