package org.example.jobseekerservice.dto.JobSeeker;

import lombok.Data;

@Data
public class JobSeekerDTO {
    private String id;
    private String userId;
    private String name;
    private String phone;
    private String field;
    private String image;
    private boolean gender;
    private String about;
    private String address;
    private boolean seeking;
    private boolean active;
}
