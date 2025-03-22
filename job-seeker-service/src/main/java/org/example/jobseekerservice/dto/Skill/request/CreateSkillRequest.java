package org.example.jobseekerservice.dto.Skill.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSkillRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Job seeker id is required")
    private String jobSeekerId;
}
