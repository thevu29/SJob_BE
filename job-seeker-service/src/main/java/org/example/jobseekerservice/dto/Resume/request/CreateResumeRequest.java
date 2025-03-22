package org.example.jobseekerservice.dto.Resume.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.jobseekerservice.annotation.validation.ValidFile;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateResumeRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @ValidFile()
    private MultipartFile file;

    private boolean main;

    @NotBlank(message = "Job seeker id is required")
    private String jobSeekerId;
}
