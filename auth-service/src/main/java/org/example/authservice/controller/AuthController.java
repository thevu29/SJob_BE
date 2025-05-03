package org.example.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.common.dto.Auth.TokenDTO;
import org.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.common.dto.Recruiter.RecruiterCreationDTO;
import org.common.dto.Recruiter.RecruiterWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.example.authservice.dto.LoginDTO;
import org.example.authservice.dto.RefreshTokenDTO;
import org.example.authservice.dto.SendOtpDTO;
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
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> registerJobSeeker(@Valid @RequestBody JobSeekerCreationDTO request) {
        JobSeekerWithUserDTO createdJobSeeker = authService.registerJobSeeker(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdJobSeeker, "Job Seeker register successfully", HttpStatus.CREATED));
    }

    @PostMapping("/register/recruiter")
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> registerRecruiter(@Valid @RequestBody RecruiterCreationDTO request) {
        RecruiterWithUserDTO createdRecruiter = authService.registerRecruiter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdRecruiter, "Recruiter register successfully", HttpStatus.CREATED));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDTO>> login(@Valid @RequestBody LoginDTO request) {
        TokenDTO tokens = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(tokens, "Login successfully", HttpStatus.OK));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Logout successfully", HttpStatus.OK));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenDTO>> refreshToken(@Valid @RequestBody RefreshTokenDTO request) {
        TokenDTO tokens = authService.refreshToken(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(tokens, "Refresh token successfully", HttpStatus.OK));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody SendOtpDTO request) {
        authService.sendOtp(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "OTP sent successfully", HttpStatus.OK));
    }
}
