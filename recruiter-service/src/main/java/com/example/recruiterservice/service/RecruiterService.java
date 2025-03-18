package com.example.recruiterservice.service;

import com.example.recruiterservice.client.UserServiceClient;
import com.example.recruiterservice.dto.RecruiterWithUserDTO;
import com.example.recruiterservice.dto.UserDTO;
import com.example.recruiterservice.dto.request.CreateRecruiterRequest;
import com.example.recruiterservice.dto.request.CreateUserRequest;
import com.example.recruiterservice.dto.response.ApiResponse;
import com.example.recruiterservice.entity.Recruiter;
import com.example.recruiterservice.mapper.RecruiterMapper;
import com.example.recruiterservice.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;
    private final UserServiceClient userServiceClient;
    private final RecruiterMapper recruiterMapper;

    public RecruiterWithUserDTO createRecruiter(CreateRecruiterRequest request) {
        UserDTO user = null;
        try{
            CreateUserRequest createUserRequest = CreateUserRequest.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role("RECRUITER")
                    .build();
            ApiResponse<UserDTO> userResponse = userServiceClient.createUser(createUserRequest);
            user = userResponse.getData();

            Recruiter recruiter = recruiterMapper.toEntity(request);
            recruiter.setUserId(user.getId());
            recruiter.setFieldId(null);
            Recruiter savedRecruiter = recruiterRepository.save(recruiter);
            return recruiterMapper.toDto(recruiterMapper.toDto(savedRecruiter), user);


        } catch (Exception e) {
            handleUserRollback(user);
            throw new RuntimeException("Failed to create recruiter", e);
        }

    }

    public void handleUserRollback(UserDTO user) {
        if (user != null && user.getId() != null) {
            try {
                log.info("Rolling back user creation: {}", user.getId());
                userServiceClient.deleteUser(user.getId());
            } catch (Exception ex) {
                log.error("Failed to roll back user creation: {}", user.getId(), ex);
            }
        }
    }
}
