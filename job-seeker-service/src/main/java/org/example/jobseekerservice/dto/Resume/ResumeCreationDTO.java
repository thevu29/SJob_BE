package org.example.jobseekerservice.dto.Resume;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.common.annotation.File.ValidFile;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResumeCreationDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @ValidFile()
    private MultipartFile file;

    private boolean main;

    @NotBlank(message = "Job seeker id is required")
    private String jobSeekerId;
}
