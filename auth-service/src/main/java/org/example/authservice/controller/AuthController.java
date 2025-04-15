package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.example.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/job-seeker")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> register(@RequestBody JobSeekerCreationDTO request) {
        JobSeekerWithUserDTO createdJobSeeker = authService.registerJobSeeker(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdJobSeeker, "Job Seeker register successfully", HttpStatus.CREATED));
    }
}
