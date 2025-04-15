package org.example.jobseekerservice.dto.Certifitcaion;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CertificationUpdateDTO {
    public String name;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    public String issueDate;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Start date must be in format yyyy-MM")
    public String expireDate;

    private MultipartFile file;
}
