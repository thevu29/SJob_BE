package org.example.jobseekerservice.dto.JobSeeker.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateJobSeekerRequest {
    private String email;
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10}$", message = "Phone number is invalid")
    private String phone;

    @Schema(type = "string", format = "binary")
    private MultipartFile imageFile;

    @NotNull(message = "Gender is required")
    private boolean gender;

    private String about;

    @NotBlank(message = "Address is required")
    private String address;

    private boolean seeking;
}
