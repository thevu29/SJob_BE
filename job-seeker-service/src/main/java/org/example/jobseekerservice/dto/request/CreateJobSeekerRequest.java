package org.example.jobseekerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateJobSeekerRequest {
    private String email;
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10}$", message = "Phone number is invalid")
    private String phone;

    private String image;

    @NotNull(message = "Gender is required")
    private boolean gender;

    private String about;

    @NotBlank(message = "Address is required")
    private String address;

    private boolean seeking;
}
