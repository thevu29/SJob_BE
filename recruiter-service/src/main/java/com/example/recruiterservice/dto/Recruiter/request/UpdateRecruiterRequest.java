package com.example.recruiterservice.dto.Recruiter.request;

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

    private Integer members;

    private Boolean status;

}
