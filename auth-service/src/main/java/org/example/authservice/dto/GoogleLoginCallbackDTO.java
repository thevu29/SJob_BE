package org.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginCallbackDTO {
    @NotBlank(message = "Code is required")
    private String code;
}
