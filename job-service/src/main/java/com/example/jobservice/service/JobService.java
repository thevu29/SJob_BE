package com.example.jobservice.service;

import com.example.jobservice.client.RecruiterServiceClient;
import com.example.jobservice.client.UserServiceClient;
import com.example.jobservice.dto.Job.JobImportDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.entity.Job;
import com.example.jobservice.entity.JobField;
import com.example.jobservice.entity.JobType;
import com.example.jobservice.mapper.JobMapper;
import com.example.jobservice.repository.FieldDetailRepository;
import com.example.jobservice.repository.JobFieldRepository;
import com.example.jobservice.repository.JobRepository;
import com.example.jobservice.utils.helpers.CSVHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.Job.JobStatus;
import org.example.common.dto.Job.JobUpdateEvent;
import org.example.common.dto.Job.JobWithRecruiterDTO;
import org.example.common.dto.Notification.NotificationEvent;
import org.example.common.dto.Notification.NotificationRequestDTO;
import org.example.common.dto.Recruiter.RecruiterDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.common.exception.FileUploadException;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final RecruiterServiceClient recruiterServiceClient;
    private final UserServiceClient userServiceClient;
    private final JobMapper jobMapper;
    private final JobFieldRepository jobFieldRepository;
    private final FieldDetailRepository fieldDetailRepository;
    private final KafkaTemplate<String, NotificationRequestDTO> kafkaTemplate;
    private final KafkaTemplate<String, JobUpdateEvent> jobUpdateKafkaTemplate;
    private final CSVHelper csvHelper;

    private String getColumnName(String sortBy) {
        switch (sortBy) {
            case "name" -> {
                return "name";
            }
            case "salary" -> {
                return "salary";
            }
            case "experience" -> {
                return "experience";
            }
            case "deadline" -> {
                return "deadline";
            }
            default -> {
                return "date";
            }
        }
    }

    private List<String> findRecruiterIds(String query, int size, String sortBy, Sort.Direction direction) {
        int recruiterPageSize = size * 2;

        ApiResponse<List<RecruiterWithUserDTO>> recruiterResponse = recruiterServiceClient.getRecruiters(
                query,
                true,
                1,
                recruiterPageSize,
                sortBy,
                direction
        );

        if (recruiterResponse == null || recruiterResponse.getData() == null || recruiterResponse.getData().isEmpty()) {
            return Collections.emptyList();
        }

        return recruiterResponse.getData().stream()
                .map(RecruiterWithUserDTO::getId)
                .toList();
    }

    private static class Range {
        Integer min;
        Integer max;

        Range(Integer min, Integer max) {
            this.min = min;
            this.max = max;
        }
    }

    private Range parseRange(String value) {
        try {
            if (value == null || value.isBlank()) return null;

            value = value.trim();
            int i = Integer.parseInt(value.substring(2).trim());

            if (value.startsWith(">=")) {
                return new Range(i, null);
            } else if (value.startsWith("<=")) {
                return new Range(null, i);
            } else if (value.startsWith("=")) {
                int exact = Integer.parseInt(value.substring(1).trim());
                return new Range(exact, exact);
            } else if (value.contains("-")) {
                String[] parts = value.split("-");
                int min = Integer.parseInt(parts[0].trim());
                int max = Integer.parseInt(parts[1].trim());
                return new Range(min, max);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<JobWithRecruiterDTO> getJobs() {
        List<Job> jobs = jobRepository.findAll();

        // Get all recruiter IDs
        List<String> recruiterIds = jobs.stream()
                .map(Job::getRecruiterId)
                .distinct()
                .toList();

        // Fetch all recruiters in one call
        Map<String, RecruiterDTO> recruiterMap = recruiterServiceClient.getRecruiterByIds(recruiterIds)
                .getData().stream()
                .collect(Collectors.toMap(RecruiterDTO::getId, recruiter -> recruiter));

        return jobs.stream()
                .map(job -> {
                    JobDTO jobDTO = jobMapper.toDto(job);
                    RecruiterDTO recruiter = recruiterMap.get(job.getRecruiterId());
                    return jobMapper.toJobWithRecruiterDTO(jobDTO, recruiter);
                })
                .toList();
    }

    public JobDTO getJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm với id:" + jobId));
        return jobMapper.toDto(job);
    }

    public List<JobDTO> getJobsByIds(List<String> ids) {
        List<Job> jobs = jobRepository.findAllById(ids);
        return jobs.stream().map(jobMapper::toDto).toList();
    }

    public Page<JobDTO> findPaginatedJobs(
            String query,
            JobType type,
            JobStatus status,
            String salary,
            String experience,
            String recruiterId,
            List<String> fieldDetailIds,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        String columnName = getColumnName(sortBy);
        Sort sort = Sort.by(direction, columnName);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        String jobStatus = status != null ? status.name() : null;
        String jobType = type != null ? type.name() : null;

        Range salaryRange = parseRange(salary);
        Range experienceRange = parseRange(experience);

        Float minSalary = salaryRange != null && salaryRange.min != null ? salaryRange.min.floatValue() : null;
        Float maxSalary = salaryRange != null && salaryRange.max != null ? salaryRange.max.floatValue() : null;

        Integer minExp = experienceRange != null ? experienceRange.min : null;
        Integer maxExp = experienceRange != null ? experienceRange.max : null;

        List<String> sanitizedFieldDetailIds = (fieldDetailIds == null || fieldDetailIds.isEmpty())
                ? Collections.emptyList()
                : fieldDetailIds;

        Page<Job> byJobName = jobRepository.findPaginatedJobs(
                query,
                Collections.emptyList(),
                jobType,
                jobStatus,
                minSalary,
                maxSalary,
                minExp,
                maxExp,
                recruiterId,
                sanitizedFieldDetailIds,
                pageable
        );

        long totalElements = byJobName.getTotalElements();

        List<Job> results = new ArrayList<>(byJobName.getContent());
        int remaining = size - results.size();

        if (remaining > 0 && query != null && !query.trim().isEmpty()) {
            List<String> recruiterIds = findRecruiterIds(query, size, sortBy, direction);

            if (!recruiterIds.isEmpty() && (recruiterId == null || recruiterId.isBlank())) {
                Page<Job> byRecruiter = jobRepository.findPaginatedJobs(
                        null,
                        recruiterIds,
                        jobType,
                        jobStatus,
                        minSalary,
                        maxSalary,
                        minExp,
                        maxExp,
                        recruiterId,
                        fieldDetailIds,
                        PageRequest.of(page, remaining, sort)
                );

                totalElements += byRecruiter.getTotalElements();

                Set<String> existingIds = results.stream()
                        .map(Job::getId)
                        .collect(Collectors.toSet());

                byRecruiter.getContent().stream()
                        .filter(job -> !existingIds.contains(job.getId()))
                        .forEach(results::add);
            }
        }

        List<JobDTO> content = results.stream().map(jobMapper::toDto).toList();

        return new PageImpl<>(content, pageable, totalElements);
    }

    public JobDTO createJob(CreateJobRequest createJobRequest, String recruiterId) {
        try {
            if (!recruiterServiceClient.checkIfRecruiterExists(recruiterId)) {
                throw new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng với id:" + recruiterId);
            }

            Job job = jobMapper.toEntity(createJobRequest);
            job.setRecruiterId(recruiterId);
            job.setStatus(JobStatus.OPEN);
            job.setDate(LocalDate.now());
            job.setCloseWhenFull(false);
            Job savedJob = jobRepository.save(job);

            // Add job fields
            for (String fieldDetailId : createJobRequest.getFieldDetails()) {
                JobField jobField = new JobField();

                Job job1 = new Job();
                job1.setId(savedJob.getId());
                jobField.setJob(job1);

                FieldDetail fieldDetail = new FieldDetail();
                fieldDetail.setId(fieldDetailId);
                jobField.setFieldDetail(fieldDetail);

                jobFieldRepository.save(jobField);
            }

            return jobMapper.toDto(savedJob);
        } catch (Exception e) {
            throw new RuntimeException("Thất bại khi tạo mới việc làm", e);
        }
    }

    public JobDTO updateJob(UpdateJobRequest updateJobRequest, String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm với id:" + jobId));
        String oldName = job.getName();
        jobMapper.updateJobFromRequest(updateJobRequest, job);


        // Update field details if provided
        if (updateJobRequest.getFieldDetails() != null) {
            updateJobFields(job, updateJobRequest.getFieldDetails());
        }

        // If name has changed, publish event
        if (!oldName.equals(job.getName())) {
            JobUpdateEvent event = new JobUpdateEvent(jobId, job.getName());
            jobUpdateKafkaTemplate.send("job-update-events", event);
        }

        Job updatedJob = jobRepository.save(job);
        return jobMapper.toDto(updatedJob);
    }

    private void updateJobFields(Job job, String[] fieldDetailIds) {
        // Get existing job fields
        List<JobField> existingJobFields = jobFieldRepository.findByJobId(job.getId());
        Set<String> existingFieldDetailIds = existingJobFields.stream()
                .map(jf -> jf.getFieldDetail().getId())
                .collect(Collectors.toSet());
        Set<String> newFieldDetailIds = new HashSet<>(Arrays.asList(fieldDetailIds));

        // Remove job fields that are no longer needed
        existingJobFields.stream()
                .filter(jf -> !newFieldDetailIds.contains(jf.getFieldDetail().getId()))
                .forEach(jobFieldRepository::delete);

        // Add new job fields
        newFieldDetailIds.stream()
                .filter(id -> !existingFieldDetailIds.contains(id))
                .forEach(fieldDetailId -> {
                    JobField jobField = JobField.builder()
                            .job(job)
                            .fieldDetail(FieldDetail.builder().id(fieldDetailId).build())
                            .build();
                    jobFieldRepository.save(jobField);
                });
    }

    public void deleteJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm với id:" + jobId));

        jobRepository.delete(job);
    }

    public void importFileCSV(MultipartFile file) {
        try {
            // Validate file
            csvHelper.validateCSVFile(file);

            // Parse CSV to DTO
            List<JobImportDTO> jobImportDTOs = csvHelper.csvToJobImportDTOs(file.getInputStream());

            // 1. Extract unique recruiter names
            Set<String> uniqueRecruiterNames = jobImportDTOs.stream()
                    .map(JobImportDTO::getRecruiter)
                    .collect(Collectors.toSet());

            // 2. Validate recruiters exist
            Map<String, String> recruiterMap = validateRecruiters(uniqueRecruiterNames);


            // 3. Extract unique field detail names (split by comma and trim)
            Set<String> uniqueFieldDetailNames = jobImportDTOs.stream()
                    .map(JobImportDTO::getFieldDetails)
                    .flatMap(fieldDetails -> Arrays.stream(fieldDetails.split("\\|")))
                    .map(String::trim)
                    .collect(Collectors.toSet());

            // 4. Validate field details exist
            Map<String, String> fieldDetailMap = validateFieldDetails(uniqueFieldDetailNames);

            // 5. Create jobs using existing method
            List<JobDTO> createdJobs = new ArrayList<>();
            try {
                for (JobImportDTO dto : jobImportDTOs) {
                    CreateJobRequest request = convertToCreateRequest(dto, fieldDetailMap);
                    JobDTO job = createJob(request, recruiterMap.get(dto.getRecruiter()));
                    createdJobs.add(job);
                }
            } catch (Exception e) {
                // Rollback: Delete all created jobs
                createdJobs.forEach(job -> {
                    try {
                        deleteJob(job.getId());
                    } catch (Exception ex) {
                        log.error("Failed to rollback job creation: {}", job.getId(), ex);
                    }
                });
                throw new RuntimeException("Nhập file thất bại: " + e.getMessage());
            }


        } catch (IOException e) {
            throw new FileUploadException("Nhập file thất bại: " + e.getMessage());
        }
    }

    private Map<String, String> validateRecruiters(Set<String> recruiterNames) {
        ApiResponse<List<RecruiterDTO>> response = recruiterServiceClient.getRecruiterByName(new ArrayList<>(recruiterNames));
        Map<String, String> recruiterMap = response.getData().stream()
                .collect(Collectors.toMap(RecruiterDTO::getName, RecruiterDTO::getId));

        Set<String> missingRecruiters = recruiterNames.stream()
                .filter(name -> !recruiterMap.containsKey(name))
                .collect(Collectors.toSet());

        if (!missingRecruiters.isEmpty()) {
            throw new FileUploadException("Không tìm thấy các nhà tuyển dụng sau: " + String.join(", ", missingRecruiters));
        }

        return recruiterMap;
    }

    private Map<String, String> validateFieldDetails(Set<String> fieldDetailNames) {
        List<FieldDetail> fieldDetails = fieldDetailRepository.findByNameIn(new ArrayList<>(fieldDetailNames));
        Map<String, String> fieldDetailMap = fieldDetails.stream()
                .collect(Collectors.toMap(FieldDetail::getName, FieldDetail::getId));

        Set<String> missingFields = fieldDetailNames.stream()
                .filter(name -> !fieldDetailMap.containsKey(name))
                .collect(Collectors.toSet());

        if (!missingFields.isEmpty()) {
            throw new FileUploadException("Không tìm thấy các lĩnh vực sau: " + String.join(", ", missingFields));
        }

        return fieldDetailMap;
    }

    private CreateJobRequest convertToCreateRequest(JobImportDTO dto, Map<String, String> fieldDetailMap) {
        String[] fieldDetailIds = Arrays.stream(dto.getFieldDetails().split("\\|"))
                .map(String::trim)
                .map(fieldDetailMap::get)
                .toArray(String[]::new);

        return CreateJobRequest.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .salary(dto.getSalary())
                .requirement(dto.getRequirement())
                .benefit(dto.getBenefit())
                .deadline(dto.getDeadline())
                .slots(dto.getSlots())
                .type(dto.getType())
                .education(dto.getEducation())
                .experience(dto.getExperience())
                .fieldDetails(fieldDetailIds)
                .build();
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Ho_Chi_Minh") // Runs daily at 9AM every day
    public void checkJobDeadlines() {
        LocalDate thresholdDate = LocalDate.now().plusDays(3);

        List<Job> expiringJobs = jobRepository.findByDeadlineAndStatus(
                thresholdDate,
                JobStatus.OPEN
        );

        expiringJobs.forEach(this::sendExpiryNotification);
    }

    private void sendExpiryNotification(Job job) {
        // Fetch recruiter details
        RecruiterWithUserDTO recruiter = recruiterServiceClient.getRecruiterById(job.getRecruiterId()).getData();
        UserDTO user = userServiceClient.getUserById(recruiter.getUserId()).getData();

        NotificationRequestDTO notification = NotificationEvent.jobExpiry(
                user.getId(),
                user.getEmail(),
                job.getId(),
                job.getName(),
                job.getDeadline()
        );

        try {
            kafkaTemplate.send("notification-requests", notification);
            log.info("Sent expiry notification for job: {}", job.getId());
        } catch (Exception e) {
            log.error("Failed to send expiry notification for job: {}", job.getId(), e);
        }
    }
}
