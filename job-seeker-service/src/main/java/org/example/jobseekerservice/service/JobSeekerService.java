package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.common.exception.ResourceNotFoundException;
import org.example.jobseekerservice.client.UserServiceClient;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerUpdateDTO;
import org.example.jobseekerservice.entity.JobSeeker;
import org.example.jobseekerservice.mapper.JobSeekerMapper;
import org.example.jobseekerservice.repository.JobSeekerRepository;
import org.example.jobseekerservice.utils.helpers.FileHelper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    private String escapeRegexSpecialChars(String input) {
        if (input == null) return "";
        return input.replaceAll("[-\\[\\]{}()*+?.,\\\\^$|#\\s]", "\\\\$0");
    }

    private UserDTO getUserById(String userId) {
        ApiResponse<UserDTO> response = userServiceClient.getUserById(userId);
        return response.getData();
    }

    private Map<String, UserDTO> fetchAndMapUsers(List<String> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        ApiResponse<List<UserDTO>> response = userServiceClient.getUsersByIds(userIds);

        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .collect(Collectors.toMap(
                            UserDTO::getId,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        }

        return Collections.emptyMap();
    }

    private List<JobSeekerWithUserDTO> mapToJobSeekerWithUserDTOs(
            List<JobSeeker> jobSeekers,
            Map<String, UserDTO> userMap
    ) {
        return jobSeekers.stream()
                .filter(js -> js.getUserId() != null && userMap.containsKey(js.getUserId()))
                .map(js -> jobSeekerMapper.toDto(
                        jobSeekerMapper.toDto(js),
                        userMap.get(js.getUserId())
                ))
                .toList();
    }

    private List<String> fetchMatchingUserIds(String query, Boolean active, int page, int size, String sortBy, Sort.Direction direction) {
        String sanitizedPattern = escapeRegexSpecialChars(query);

        ApiResponse<List<UserDTO>> usersResponse = userServiceClient.findPagedUsers(
                sanitizedPattern,
                active,
                "JOB_SEEKER",
                page,
                size,
                sortBy,
                direction
        );

        if (usersResponse != null && usersResponse.getData() != null && !usersResponse.getData().isEmpty()) {
            return usersResponse.getData().stream()
                    .map(UserDTO::getId)
                    .toList();
        }

        return Collections.emptyList();
    }

    public Page<JobSeekerWithUserDTO> findPagedJobSeekers(
            String query,
            Boolean active,
            Boolean seeking,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        String columnName = switch (sortBy) {
            case "name" -> "name";
            case "updatedAt" -> "updated_at";
            case "createdAt" -> "created_at";
            default -> "id";
        };

        Sort sort = Sort.by(direction, columnName);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        boolean isActiveFilter = active != null;

        List<String> matchingUserIds = fetchMatchingUserIds(query, active, page, size, sortBy, direction);

        if (matchingUserIds.isEmpty() && isActiveFilter) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        Page<JobSeeker> jobSeekers = jobSeekerRepository.findBySearchCriteria(
                query,
                matchingUserIds,
                seeking,
                pageable
        );

        if (jobSeekers.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        List<String> userIds = jobSeekers.getContent().stream()
                .map(JobSeeker::getUserId)
                .distinct()
                .toList();

        Map<String, UserDTO> userMap = fetchAndMapUsers(userIds);

        List<JobSeekerWithUserDTO> content = mapToJobSeekerWithUserDTOs(jobSeekers.getContent(), userMap);

        return new PageImpl<>(content, pageable, jobSeekers.getTotalElements());
    }

    public List<JobSeekerWithUserDTO> getAllJobSeekers() {
        List<JobSeeker> jobSeekers = jobSeekerRepository.findAll();

        List<String> userIds = jobSeekers.stream()
                .map(JobSeeker::getUserId)
                .toList();

        Map<String, UserDTO> userMap = fetchAndMapUsers(userIds);

        return mapToJobSeekerWithUserDTOs(jobSeekers, userMap);
    }

    public JobSeekerWithUserDTO getJobSeekerById(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        UserDTO user = getUserById(jobSeeker.getUserId());

        return jobSeekerMapper.toDto(jobSeekerMapper.toDto(jobSeeker), user);
    }

    public JobSeekerWithUserDTO createJobSeeker(JobSeekerCreationDTO request) {
        UserDTO user = null;

        try {
            UserCreationDTO userCreationRequest = UserCreationDTO.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role("JOB_SEEKER")
                    .build();

            ApiResponse<UserDTO> response = userServiceClient.createUser(userCreationRequest);
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

    public JobSeekerWithUserDTO updateJobSeeker(String id, JobSeekerUpdateDTO request) {
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
