package org.common.dto.Recruiter;

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
}
