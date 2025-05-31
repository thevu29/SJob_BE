package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.example.authservice.dto.GoogleLoginDTO;
import org.example.common.dto.Auth.TokenDTO;
import org.example.common.dto.Email.EmailMessageDTO;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.User.UserUpdateOtpDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.authservice.client.JobSeekerServiceClient;
import org.example.authservice.client.RecruiterServiceClient;
import org.example.authservice.client.UserServiceClient;
import org.example.authservice.dto.LoginDTO;
import org.example.authservice.dto.RefreshTokenDTO;
import org.example.authservice.dto.SendOtpDTO;
import org.example.authservice.keycloak.KeycloakService;
import org.example.authservice.utils.Generate;
import org.example.common.enums.UserRole;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KeycloakService keycloakService;
    private final UserServiceClient userServiceClient;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;
    private final KafkaTemplate<String, EmailMessageDTO> kafkaTemplate;

    public UserDTO getUserByEmail(String email) {
        ApiResponse<UserDTO> response = userServiceClient.getUserByEmail(email);
        return response.getData();
    }

    public JobSeekerWithUserDTO getJobSeekerByEmail(String email) {
        ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.getJobSeekerByEmail(email);
        return response.getData();
    }

    public RecruiterWithUserDTO getRecruiterByEmail(String email) {
        ApiResponse<RecruiterWithUserDTO> response = recruiterServiceClient.getRecruiterByEmail(email);
        return response.getData();
    }

    public JobSeekerWithUserDTO registerJobSeeker(JobSeekerCreationDTO request) {
        keycloakService.createJobSeeker(request);
        ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.createJobSeeker(request);
        return response.getData();
    }

    public RecruiterWithUserDTO registerRecruiter(RecruiterCreationDTO request) {
        keycloakService.createRecruiter(request);
        ApiResponse<RecruiterWithUserDTO> response = recruiterServiceClient.createRecruiter(request);
        return response.getData();
    }

    public TokenDTO login(LoginDTO request) {
        ApiResponse<UserDTO> response = userServiceClient.getUserByEmail(request.getEmail());

        UserDTO user = response.getData();

        if (user.getGoogleId() != null) {
            throw new IllegalArgumentException("Tài khoản này được đăng nhập bằng Google");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên để biết thêm chi tiết");
        }

        if (user.getRole() == UserRole.RECRUITER) {
            ApiResponse<RecruiterWithUserDTO> recruiterResponse = recruiterServiceClient.getRecruiterByEmail(user.getEmail());

            if (recruiterResponse.getData().getStatus() != true) {
                throw new IllegalArgumentException("Tài khoản của bạn chưa được duyệt, vui lòng chờ quản trị viên phê duyệt");
            }
        }

        return keycloakService.login(request);
    }

    public TokenDTO handleGoogleCallback(String code) {
        GoogleLoginDTO googleLogin = keycloakService.googleLogin(code);

        JobSeekerCreationDTO jobSeekerCreationDTO = JobSeekerCreationDTO.builder()
                .email(googleLogin.getEmail())
                .name(googleLogin.getName())
                .password(googleLogin.getGoogleId())
                .image(googleLogin.getImage())
                .googleId(googleLogin.getGoogleId())
                .build();

        ApiResponse<JobSeekerWithUserDTO> jobSeekerResponse = jobSeekerServiceClient.getOrCreateJobSeekerByEmail(jobSeekerCreationDTO);

        if (jobSeekerResponse.getData() == null) {
            throw new IllegalArgumentException("Không thể tạo tài khoản Job Seeker");
        }

        return TokenDTO.builder()
                .accessToken(googleLogin.getAccessToken())
                .refreshToken(googleLogin.getRefreshToken())
                .expiresIn(googleLogin.getExpiresIn())
                .build();
    }

    public TokenDTO refreshToken(RefreshTokenDTO request) {
        return keycloakService.refreshToken(request.getRefreshToken());
    }

    public void logout(String refreshToken) {
        keycloakService.logout(refreshToken);
    }

    public void sendOtp(SendOtpDTO request) {
        ApiResponse<UserDTO> response = userServiceClient.getUserByEmail(request.getEmail());

        UserDTO user = response.getData();

        if (user.getGoogleId() != null) {
            throw new IllegalArgumentException("Tài khoản này được đăng nhập bằng Google, không thể gửi OTP");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên để biết thêm chi tiết");
        }

        try {
            String otp = Generate.generateOtp();

            EmailMessageDTO emailMessageDTO = EmailMessageDTO.builder()
                    .to(request.getEmail())
                    .subject("Mã OTP xác thực")
                    .body("Mã OTP của bạn là: " + otp)
                    .build();

            kafkaTemplate.send("send-email", emailMessageDTO).get();

            UserUpdateOtpDTO userUpdateDTO = UserUpdateOtpDTO.builder()
                    .email(request.getEmail())
                    .otp(otp)
                    .otpExpiresAt(LocalDateTime.now().plusMinutes(5))
                    .build();

            userServiceClient.updateUserOTP(userUpdateDTO);
        } catch (Exception e) {
            throw new KafkaException("Failed to send email message to Kafka", e);
        }
    }
}
