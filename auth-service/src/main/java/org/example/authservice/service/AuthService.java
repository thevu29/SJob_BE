package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.example.authservice.client.JobSeekerServiceClient;
import org.example.authservice.keycloak.KeycloakService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KeycloakService keycloakService;
    private final JobSeekerServiceClient jobSeekerServiceClient;

    public JobSeekerWithUserDTO registerJobSeeker(JobSeekerCreationDTO request) {
        keycloakService.createJobSeeker(request);
        ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.createJobSeeker(request);
        return response.getData();
    }
}
