package org.example.reportservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class CreateReportDTO {
    private String jobSeekerId;

    private String recruiterId;

    @NotBlank(message = "Lời nhắn không được để trống")
    private String message;

    @NotBlank(message = "Lý do không được để trống")
    private String reason;

    @Schema(type = "string", format = "binary")
    private MultipartFile evidenceFile;
}
