package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerUpdateEvent;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.common.exception.ResourceNotFoundException;
import org.example.jobseekerservice.client.UserServiceClient;
import org.example.jobseekerservice.dto.JobSeeker.GetJobSeekerStatisticsDTO;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerUpdateDTO;
import org.example.jobseekerservice.entity.JobSeeker;
import org.example.jobseekerservice.mapper.JobSeekerMapper;
import org.example.jobseekerservice.repository.JobSeekerRepository;
import org.example.jobseekerservice.utils.helpers.FileHelper;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerService {
    private final FileHelper fileHelper;
    private final JobSeekerMapper jobSeekerMapper;
    private final UserServiceClient userServiceClient;
    private final JobSeekerRepository jobSeekerRepository;
    private final KafkaTemplate<String, JobSeekerUpdateEvent> jobSeekerUpdateKafkaTemplate;

    public GetJobSeekerStatisticsDTO getJobSeekerCountInMonth() {
        LocalDate date = LocalDate.now();
        int month = date.getMonthValue();
        int year = date.getYear();

        Integer count = jobSeekerRepository.countJobSeekersInMonth(month, year);

        if (count == null) {
            count = 0;
        }

        Integer lastMonthCount = jobSeekerRepository.countJobSeekersInMonth(month - 1, year);

        if (lastMonthCount == null) {
            lastMonthCount = 0;
        }

        double percentageChange = 0.0;
        if (lastMonthCount > 0) {
            percentageChange = ((double) (count - lastMonthCount) / lastMonthCount) * 100.0;
        } else if (count > 0) {
            percentageChange = 100.0;
        }

        return GetJobSeekerStatisticsDTO.builder()
                .month(month)
                .jobSeekers(count)
                .percentageChange(percentageChange)
                .build();
    }

    public List<GetJobSeekerStatisticsDTO> getJobSeekerStatistics() {
        int year = Year.now().getValue();

        List<GetJobSeekerStatisticsDTO> result = new ArrayList<>();

        List<Object[]> monthlyData = jobSeekerRepository.countJobSeekersByMonthInYear(year);

        Map<Integer, Long> countsByMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            countsByMonth.put(i, 0L);
        }

        for (Object[] row : monthlyData) {
            int month = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            countsByMonth.put(month, count);
        }

        for (int month = 1; month <= 12; month++) {
            GetJobSeekerStatisticsDTO monthData = GetJobSeekerStatisticsDTO.builder()
                    .month(month)
                    .jobSeekers(countsByMonth.get(month).intValue())
                    .build();

            result.add(monthData);
        }

        return result;
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
        String sanitizedQuery = query == null ? "" : query;

        List<String> matchingUserIds = fetchMatchingUserIds(sanitizedQuery, active, size, sortBy, direction);

        if (matchingUserIds.isEmpty() && isActiveFilter) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        Page<JobSeeker> jobSeekers = jobSeekerRepository.findBySearchCriteria(
                sanitizedQuery,
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

    public JobSeekerWithUserDTO getJobSeekerByEmail(String email) {
        ApiResponse<UserDTO> userResponse = userServiceClient.getUserByEmail(email);
        UserDTO user = userResponse.getData();

        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy user");
        }

        JobSeeker jobSeeker = jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy job seeker"));

        return jobSeekerMapper.toDto(jobSeekerMapper.toDto(jobSeeker), user);
    }

    public JobSeekerWithUserDTO getOrCreateJobSeeker(JobSeekerCreationDTO request) {
        UserCreationDTO userCreationRequest = UserCreationDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role("JOB_SEEKER")
                .googleId(request.getGoogleId())
                .build();

        ApiResponse<UserDTO> userResponse = userServiceClient.getOrCreateUserByEmail(userCreationRequest);
        UserDTO user = userResponse.getData();

        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy hoặc tạo được user");
        }

        try {
            JobSeeker jobSeeker = jobSeekerRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        JobSeeker newJobSeeker = jobSeekerMapper.toEntity(request);
                        newJobSeeker.setUserId(user.getId());

                        return jobSeekerRepository.save(newJobSeeker);
                    });

            return jobSeekerMapper.toDto(jobSeekerMapper.toDto(jobSeeker), user);
        } catch (Exception e) {
            handleUserRollback(user);
            throw new RuntimeException("Tạo job seeker thất bại", e);
        }
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
            throw new RuntimeException("Tạo job seeker thất bại", e);
        }
    }

    public JobSeekerWithUserDTO updateJobSeeker(String id, JobSeekerUpdateDTO request) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy job seeker"));

        String oldName = jobSeeker.getName();
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

        if (!oldName.equals(jobSeeker.getName()) || !jobSeeker.getField().equals(request.getField())) {
            JobSeekerUpdateEvent event = new JobSeekerUpdateEvent(id, jobSeeker.getName());
            jobSeekerUpdateKafkaTemplate.send("job-seeker-update-events", event);
        }


        JobSeeker updatedJobSeeker = jobSeekerRepository.save(jobSeeker);

        return jobSeekerMapper.toDto(jobSeekerMapper.toDto(updatedJobSeeker), user);
    }

    public void deleteJobSeeker(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy job seeker"));

        UserDTO user = getUserById(jobSeeker.getUserId());

        jobSeekerRepository.delete(jobSeeker);
        userServiceClient.deleteUser(user.getId());
    }


    private void handleUserRollback(UserDTO user) {
        if (user != null && user.getId() != null) {
            try {
                userServiceClient.deleteUser(user.getId());
            } catch (Exception ex) {
                log.error("Failed to roll back user creation: {}", user.getId(), ex);
            }
        }
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

    private List<String> fetchMatchingUserIds(String query, Boolean active, int size, String sortBy, Sort.Direction direction) {
        ApiResponse<List<UserDTO>> usersResponse = userServiceClient.findPagedUsers(
                query,
                active,
                "JOB_SEEKER",
                1,
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
}
