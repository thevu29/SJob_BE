package org.example.applicationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateApplicationDTO {
    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}
