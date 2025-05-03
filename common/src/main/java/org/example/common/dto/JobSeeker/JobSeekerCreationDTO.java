package org.example.common.dto.JobSeeker;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobSeekerCreationDTO {
    private String email;

    private String password;

    @NotBlank(message = "Name is required")
    private String name;
}
