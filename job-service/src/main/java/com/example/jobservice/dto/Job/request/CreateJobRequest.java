package com.example.jobservice.dto.Job.request;

import com.example.jobservice.dto.FieldDetail.FieldDetailDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateJobRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be greater than or equal to 0")
    private Double salary;

    @NotBlank(message = "Requirement is required")
    private String requirement;

    @NotBlank(message = "Benefit is required")
    private String benefit;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be a future date")
    private LocalDate deadline;

    @NotNull(message = "Slots is required")
    @Min(value = 1, message = "Slots must be at least 1")
    private Integer slots;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Education is required")
    private String education;

    @NotBlank(message = "Experience is required")
    private String experience;

    @NotNull(message = "Field Detail is required")
    private String[] fieldDetails;
}
