package com.example.recruiterservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateRecruiterRequest {

    private String name;

    private String fieldId;

    private String about;

    private MultipartFile image;

    private String website;

    private String address;

    private int members;



}
