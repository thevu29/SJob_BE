package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobseekerservice.client.UserServiceClient;
import org.example.jobseekerservice.dto.JobSeekerWithUserDTO;
import org.example.jobseekerservice.dto.UserDTO;
import org.example.jobseekerservice.dto.request.CreateJobSeekerRequest;
import org.example.jobseekerservice.dto.request.CreateUserRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.entity.JobSeeker;
import org.example.jobseekerservice.mapper.JobSeekerMapper;
import org.example.jobseekerservice.repository.JobSeekerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerService {
    private final JobSeekerRepository jobSeekerRepository;
    private final UserServiceClient userServiceClient;
    private final JobSeekerMapper jobSeekerMapper;

    public List<JobSeekerWithUserDTO> getJobSeekers() {
        List<JobSeeker> jobSeekers = jobSeekerRepository.findAll();

        List<String> userIds = jobSeekers.stream()
                .map(JobSeeker::getUserId)
                .collect(Collectors.toList());

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);

        Map<String, UserDTO> userMap = users.stream()
                .filter(user -> user.getDeletedAt() == null)
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        return jobSeekers.stream()
                .filter(js -> userMap.containsKey(js.getUserId()))
                .map(js -> jobSeekerMapper.toDto(
                        jobSeekerMapper.toDto(js),
                        userMap.get(js.getUserId())
                ))
                .collect(Collectors.toList());
    }

    public JobSeekerWithUserDTO createJobSeeker(CreateJobSeekerRequest request) {
        UserDTO user = null;

        try {
            CreateUserRequest createUserRequest = CreateUserRequest.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role("JOB_SEEKER")
                    .build();

            ApiResponse<UserDTO> response = userServiceClient.createUser(createUserRequest);
            user = response.getData();

            JobSeeker jobSeeker = jobSeekerMapper.toEntity(request);
            jobSeeker.setUserId(user.getId());

            JobSeeker savedJobSeeker = jobSeekerRepository.save(jobSeeker);

            return jobSeekerMapper.toDto(jobSeekerMapper.toDto(savedJobSeeker), user);
        } catch (Exception e) {
            handleUserRollback(user);
            throw new RuntimeException("Failed to create job seeker", e);
        }
    }

    private void handleUserRollback(UserDTO user) {
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
