package com.example.recruiterservice.dto.Recruiter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterImportDTO {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Tên ngành nghề không được để trống")
    private String fieldName;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Giới thiệu không được để trống")
    private String about;

    private String image;

    @NotBlank(message = "Website không được để trống")
    private String website;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Min(value = 1, message = "Số lượng nhân sự phải lớn hơn 0")
    private int members;
}
