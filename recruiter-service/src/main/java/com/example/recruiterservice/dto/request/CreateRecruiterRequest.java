package com.example.recruiterservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class CreateRecruiterRequest {
    private String email;
    private String password;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Ngành nghề/Lĩnh vực không được để trống")
    private String fieldId;

    @NotNull(message = "Giới thiệu không được để trống")
    private String about;

    private MultipartFile image;

    @NotNull(message = "Website không được để trống")
    private String website;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Số lượng nhân sự không được để trống")
    @Positive(message = "Số lượng nhân sự phải lớn hơn 0")
    private int members;
}
