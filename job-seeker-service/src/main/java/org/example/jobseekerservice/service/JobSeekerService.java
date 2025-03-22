package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobseekerservice.client.UserServiceClient;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.jobseekerservice.dto.JobSeeker.UserDTO;
import org.example.jobseekerservice.dto.JobSeeker.request.CreateJobSeekerRequest;
import org.example.jobseekerservice.dto.JobSeeker.request.CreateUserRequest;
import org.example.jobseekerservice.dto.JobSeeker.request.UpdateJobSeekerRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.entity.JobSeeker;
import org.example.jobseekerservice.exception.ResourceNotFoundException;
import org.example.jobseekerservice.mapper.JobSeekerMapper;
import org.example.jobseekerservice.repository.JobSeekerRepository;
import org.example.jobseekerservice.utils.helpers.FileHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final FileHelper fileHelper;

    private UserDTO getUserById(String userId) {
        ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
        return response.getData();
    }

    public List<JobSeekerWithUserDTO> getJobSeekers() {
        List<JobSeeker> jobSeekers = jobSeekerRepository.findAll();

        List<String> userIds = jobSeekers.stream()
                .map(JobSeeker::getUserId)
                .collect(Collectors.toList());

        ApiResponse<List<UserDTO>> response = userServiceClient.getUsersByIds(userIds);
        List<UserDTO> users = response.getData();

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

    public JobSeekerWithUserDTO getJobSeekerById(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        UserDTO user = getUserById(jobSeeker.getUserId());

        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Job Seeker not found");
        }

        return jobSeekerMapper.toDto(jobSeekerMapper.toDto(jobSeeker), user);
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

            if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                try {
                    String imageUrl = fileHelper.uploadFile(request.getImageFile());
                    jobSeeker.setImage(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }

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

    public JobSeekerWithUserDTO updateJobSeeker(String id, UpdateJobSeekerRequest request) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        UserDTO user = getUserById(jobSeeker.getUserId());

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            try {
                if (jobSeeker.getImage() != null && !jobSeeker.getImage().isEmpty()) {
                    fileHelper.deleteFile(jobSeeker.getImage());
                }

                String imageUrl = fileHelper.uploadFile(request.getImageFile());
                jobSeeker.setImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        jobSeekerMapper.toEntity(request, jobSeeker);
        JobSeeker updatedJobSeeker = jobSeekerRepository.save(jobSeeker);

        return jobSeekerMapper.toDto(jobSeekerMapper.toDto(updatedJobSeeker), user);
    }

    public void deleteJobSeeker(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        UserDTO user = getUserById(jobSeeker.getUserId());

        jobSeekerRepository.delete(jobSeeker);
        userServiceClient.deleteUser(user.getId());
    }
}
