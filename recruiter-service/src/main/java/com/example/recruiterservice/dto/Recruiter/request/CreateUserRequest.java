package com.example.recruiterservice.dto.Recruiter.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    private String email;
    private String password;
    private String role;
}
