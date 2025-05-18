package org.example.applicationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewedJobCreationDTO {
    @NotBlank(message = "Job seeker is required")
    private String jobSeekerId;

    @NotBlank(message = "Job is required")
    private String jobId;
}
