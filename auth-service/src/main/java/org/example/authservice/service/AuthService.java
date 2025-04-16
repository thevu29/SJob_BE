package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.common.dto.Auth.TokenDTO;
import org.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.common.dto.Recruiter.RecruiterCreationDTO;
import org.common.dto.Recruiter.RecruiterWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.example.authservice.client.JobSeekerServiceClient;
import org.example.authservice.client.RecruiterServiceClient;
import org.example.authservice.dto.LoginDTO;
import org.example.authservice.keycloak.KeycloakService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KeycloakService keycloakService;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;

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
}
