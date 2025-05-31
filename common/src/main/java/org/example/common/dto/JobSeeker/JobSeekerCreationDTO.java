package org.example.common.dto.JobSeeker;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerCreationDTO {
    private String email;

    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    private String googleId;

    private String image;
}
