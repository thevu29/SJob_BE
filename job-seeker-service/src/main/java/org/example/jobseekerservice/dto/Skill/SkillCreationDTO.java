package org.example.jobseekerservice.dto.Skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillCreationDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Job seeker id is required")
    private String jobSeekerId;
}
