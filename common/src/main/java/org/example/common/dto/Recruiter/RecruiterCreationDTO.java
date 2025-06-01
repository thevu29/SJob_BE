package org.example.common.dto.Recruiter;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterCreationDTO {
    private String email;

    private String password;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Lĩnh vực không được để trống")
    private String fieldId;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String website;
}
