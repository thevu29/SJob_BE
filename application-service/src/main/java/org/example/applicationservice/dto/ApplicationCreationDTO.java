package org.example.applicationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreationDTO {
    @NotBlank(message = "Job seeker is required")
    private String jobSeekerId;

    @NotBlank(message = "Job is required")
    private String jobId;

    private String resumeId;

    @Schema(type = "string", format = "binary")
    private MultipartFile resumeFile;

    private String message;
}
