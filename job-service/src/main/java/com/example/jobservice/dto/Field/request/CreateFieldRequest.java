package com.example.jobservice.dto.Field.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFieldRequest {
    @NotBlank(message = "Tên Ngành nghề/Lĩnh vực không được để trống")
    private String name;

    private String description;
}
