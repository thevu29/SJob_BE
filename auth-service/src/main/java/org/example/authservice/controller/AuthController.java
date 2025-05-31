package org.example.authservice.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.GoogleLoginCallbackDTO;
import org.example.authservice.utils.JwtUtil;
import org.example.common.dto.Auth.TokenDTO;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.authservice.dto.LoginDTO;
import org.example.authservice.dto.RefreshTokenDTO;
import org.example.authservice.dto.SendOtpDTO;
import org.example.authservice.service.AuthService;
import org.example.common.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(HttpServletRequest request) {
        try {
            String token = JwtUtil.extractTokenFromHeader(request);
            DecodedJWT jwt = JwtUtil.decodeJwt(token);

            Object user;
            String email = jwt.getClaim("email").asString();
            UserRole role = JwtUtil.getRoleFromToken(token);

            if (email == null || email.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Email not found in token", HttpStatus.UNAUTHORIZED));
            }

            if (role == UserRole.JOB_SEEKER) {
                user = authService.getJobSeekerByEmail(email);
            } else if (role == UserRole.RECRUITER) {
                user = authService.getRecruiterByEmail(email);
            } else {
                user = authService.getUserByEmail(email);
            }

            return ResponseEntity.ok(
                    ApiResponse.success(user, "Lấy thông tin người dùng hiện tại thành công", HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: " + e.getMessage(), HttpStatus.UNAUTHORIZED));
        }
    }

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

    @PostMapping("/google/callback")
    public ResponseEntity<ApiResponse<TokenDTO>> googleLoginCallback(@RequestBody GoogleLoginCallbackDTO request) {
        TokenDTO tokens = authService.handleGoogleCallback(request.getCode());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(tokens, "Google login successfully", HttpStatus.OK));
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
