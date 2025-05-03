package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.example.common.dto.Auth.TokenDTO;
import org.example.common.dto.Email.EmailMessageDTO;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
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
        return keycloakService.login(request);
    }

    public TokenDTO refreshToken(RefreshTokenDTO request) {
        return keycloakService.refreshToken(request.getRefreshToken());
    }

    public void logout(String refreshToken) {
        keycloakService.logout(refreshToken);
    }

    public void sendOtp(SendOtpDTO request) {
        try {
            String otp = Generate.generateOtp();

            EmailMessageDTO emailMessageDTO = EmailMessageDTO.builder()
                    .to(request.getEmail())
                    .subject("OTP for Job Portal")
                    .body("Your OTP is: " + otp)
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
