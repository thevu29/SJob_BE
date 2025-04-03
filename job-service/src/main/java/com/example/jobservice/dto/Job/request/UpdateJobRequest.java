package com.example.jobservice.dto.Job.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateJobRequest {

    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be greater than or equal to 0")
    private Double salary;

    private String requirement;

    private String benefit;

    @Future(message = "Deadline must be a future date")
    private LocalDate deadline;

    @Min(value = 1, message = "Slots must be at least 1")
    private Integer slots;

    private String type;

    private String education;

    private String experience;

    private String[] fieldDetails;

}
