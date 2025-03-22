package org.example.jobseekerservice.dto.Certifitcaion.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCertificationRequest {
    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Issue date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    public String issueDate;

    @NotBlank(message = "Expire date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    public String expireDate;

    @NotBlank(message = "Job seeker id is required")
    public String jobSeekerId;

    private MultipartFile file;
}
