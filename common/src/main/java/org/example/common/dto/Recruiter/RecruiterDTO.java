package org.example.common.dto.Recruiter;

import lombok.Data;

@Data
public class RecruiterDTO {
    private String id;
    private String userId;
    private String fieldId;
    private String name;
    private String about;
    private String image;
    private String website;
    private String address;
    private Integer members;
    private Boolean status;
}
