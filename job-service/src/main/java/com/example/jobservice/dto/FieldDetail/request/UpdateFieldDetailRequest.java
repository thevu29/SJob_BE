package com.example.jobservice.dto.FieldDetail.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFieldDetailRequest {
    private String name;

    @NotBlank(message = "Ngành nghề/Lĩnh vực không được để trống")
    private String fieldId;
}
