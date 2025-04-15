package com.example.recruiterservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateRecruiterRequest {
    private String email;
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "About is required")
    private String about;

    private String image;

    @NotNull(message = "Website is required")
    private String website;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Members is required")
    @Positive(message = "Members must be positive")
    private int members;


}
