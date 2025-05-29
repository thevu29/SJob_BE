package com.example.jobservice.service;

import com.example.jobservice.client.RecruiterServiceClient;
import com.example.jobservice.client.UserServiceClient;
import com.example.jobservice.dto.Job.JobImportDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.*;
import com.example.jobservice.entity.QJob;
import com.example.jobservice.entity.QJobField;
import com.example.jobservice.mapper.JobMapper;
import com.example.jobservice.repository.FieldDetailRepository;
import com.example.jobservice.repository.JobFieldRepository;
import com.example.jobservice.repository.JobRepository;
import com.example.jobservice.utils.helpers.CSVHelper;
import com.example.jobservice.utils.helpers.RangeFilter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    private final JPAQueryFactory queryFactory;
    private final JobRepository jobRepository;
    private final RecruiterServiceClient recruiterServiceClient;
    private final UserServiceClient userServiceClient;
    private final JobMapper jobMapper;
    private final JobFieldRepository jobFieldRepository;
    private final FieldDetailRepository fieldDetailRepository;
    private final KafkaTemplate<String, NotificationRequestDTO> kafkaTemplate;
    private final KafkaTemplate<String, JobUpdateEvent> jobUpdateKafkaTemplate;
    private final CSVHelper csvHelper;

    private List<JobWithRecruiterDTO> convertToJobWithRecruiterDTO(List<Job> jobs, Map<String, RecruiterDTO> recruiterMap) {
        return jobs.stream()
                .map(job -> {
                    JobDTO jobDTO = jobMapper.toDto(job);
                    RecruiterDTO recruiter = recruiterMap.get(job.getRecruiterId());
                    return jobMapper.toJobWithRecruiterDTO(jobDTO, recruiter);
                })
                .collect(Collectors.toList());
    }

    private Page<JobWithRecruiterDTO> convertJobsToDTO(List<Job> jobs, Pageable pageable, long totalElements) {
        if (jobs.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, totalElements);
        }

        List<String> recruiterIds = jobs.stream()
                .map(Job::getRecruiterId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, RecruiterDTO> recruiterMap = getRecruiterMap(recruiterIds);

        List<JobWithRecruiterDTO> jobWithRecruiterDTOs = convertToJobWithRecruiterDTO(jobs, recruiterMap);

        return new PageImpl<>(jobWithRecruiterDTOs, pageable, totalElements);
    }

    private Map<String, RecruiterDTO> getRecruiterMap(List<String> recruiterIds) {
        try {
            if (recruiterIds == null || recruiterIds.isEmpty()) {
                return Map.of();
            }

            ApiResponse<List<RecruiterDTO>> response = recruiterServiceClient.getRecruiterByIds(recruiterIds);

            if (response != null && response.getData() != null) {
                return response.getData().stream().collect(
                        Collectors.toMap(RecruiterDTO::getId, recruiter -> recruiter)
                );
            }
        } catch (Exception e) {
            System.err.println("Error fetching recruiter names: " + e.getMessage());
        }

        return Map.of();
    }

    private void applyCommonFilters(
            BooleanBuilder predicate,
            QJob job,
            QJobField jobField,
            JobType type,
            JobStatus status,
            String recruiterId,
            List<String> fieldDetailIds
    ) {
        if (type != null) {
            predicate.and(job.type.eq(type));
        }

        if (status != null) {
            predicate.and(job.status.eq(status));
        }

        if (recruiterId != null && !recruiterId.trim().isEmpty()) {
            predicate.and(job.recruiterId.eq(recruiterId));
        }

        if (fieldDetailIds != null && !fieldDetailIds.isEmpty()) {
            predicate.and(jobField.fieldDetail.id.in(fieldDetailIds));
        }
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy, Sort.Direction direction) {
        PathBuilder<Job> pathBuilder = new PathBuilder<>(Job.class, "job");

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "date";
        }

        com.querydsl.core.types.Order order = direction == Sort.Direction.ASC ?
                com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

        return new OrderSpecifier<>(order, pathBuilder.getString(sortBy));
    }

    private List<Job> findJobsByNameQuery(
            String query,
            JobType type,
            JobStatus status,
            String salary,
            String experience,
            String recruiterId,
            List<String> fieldDetailIds,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        QJob job = QJob.job;
        QJobField jobField = QJobField.jobField;

        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(job.name.containsIgnoreCase(query));

        applyCommonFilters(predicate, job, jobField, type, status, recruiterId, fieldDetailIds);

        List<Job> allJobs = queryFactory.selectFrom(job)
                .distinct()
                .leftJoin(job.jobFields, jobField)
                .where(predicate)
                .orderBy(getOrderSpecifier(sortBy, direction))
                .fetch();

        return allJobs.stream()
                .filter(j -> RangeFilter.matchesFilter(j.getSalary(), salary))
                .filter(j -> RangeFilter.matchesFilter(j.getExperience(), experience))
                .limit(size)
                .collect(Collectors.toList());
    }

    private long countJobsByNameQuery(
            String query,
            JobType type,
            JobStatus status,
            String salary,
            String experience,
            String recruiterId,
            List<String> fieldDetailIds
    ) {
        QJob job = QJob.job;
        QJobField jobField = QJobField.jobField;

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(job.name.containsIgnoreCase(query));
        applyCommonFilters(predicate, job, jobField, type, status, recruiterId, fieldDetailIds);

        List<Job> allJobs = queryFactory.selectFrom(job)
                .distinct()
                .leftJoin(job.jobFields, jobField)
                .where(predicate)
                .fetch();

        return allJobs.stream()
                .filter(j -> RangeFilter.matchesFilter(j.getSalary(), salary))
                .filter(j -> RangeFilter.matchesFilter(j.getExperience(), experience))
                .count();
    }

    private List<Job> findJobsByRecruiterNameQuery(
            String query,
            JobType type,
            JobStatus status,
            String salary,
            String experience,
            String recruiterId,
            List<String> fieldDetailIds,
            int size,
            String sortBy,
            Sort.Direction direction,
            List<Job> excludeJobs
    ) {
        QJob job = QJob.job;

        List<String> allRecruiterIds = queryFactory.select(job.recruiterId)
                .from(job)
                .distinct()
                .fetch();

        Map<String, RecruiterDTO> recruiterMaps = getRecruiterMap(allRecruiterIds);

        Set<String> matchingRecruiterIds = recruiterMaps.entrySet().stream()
                .filter(entry -> entry.getValue().getName().toLowerCase().contains(query.toLowerCase()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (matchingRecruiterIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> excludeJobIds = excludeJobs.stream()
                .map(Job::getId)
                .collect(Collectors.toSet());

        QJobField jobField = QJobField.jobField;
        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(job.recruiterId.in(matchingRecruiterIds));

        if (!excludeJobIds.isEmpty()) {
            predicate.and(job.id.notIn(excludeJobIds));
        }

        applyCommonFilters(predicate, job, jobField, type, status, recruiterId, fieldDetailIds);

        List<Job> allJobs = queryFactory.selectFrom(job)
                .distinct()
                .leftJoin(job.jobFields, jobField)
                .where(predicate)
                .orderBy(getOrderSpecifier(sortBy, direction))
                .fetch();

        return allJobs.stream()
                .filter(j -> RangeFilter.matchesFilter(j.getSalary(), salary))
                .filter(j -> RangeFilter.matchesFilter(j.getExperience(), experience))
                .limit(size)
                .collect(Collectors.toList());
    }

    private long countJobsByRecruiterNameQuery(
            String query,
            JobType type,
            JobStatus status,
            String salary,
            String experience,
            String recruiterId,
            List<String> fieldDetailIds,
            List<Job> excludeJobs
    ) {
        QJob job = QJob.job;

        List<String> allRecruiterIds = queryFactory.select(job.recruiterId)
                .from(job)
                .distinct()
                .fetch();

        Map<String, RecruiterDTO> recruiterMaps = getRecruiterMap(allRecruiterIds);

        Set<String> matchingRecruiterIds = recruiterMaps.entrySet().stream()
                .filter(entry -> entry.getValue().getName().toLowerCase().contains(query.toLowerCase()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (matchingRecruiterIds.isEmpty()) {
            return 0;
        }

        Set<String> excludeJobIds = excludeJobs.stream()
                .map(Job::getId)
                .collect(Collectors.toSet());

        QJobField jobField = QJobField.jobField;
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(job.recruiterId.in(matchingRecruiterIds));

        if (!excludeJobIds.isEmpty()) {
            predicate.and(job.id.notIn(excludeJobIds));
        }

        applyCommonFilters(predicate, job, jobField, type, status, recruiterId, fieldDetailIds);

        List<Job> allJobs = queryFactory.selectFrom(job)
                .distinct()
                .leftJoin(job.jobFields, jobField)
                .where(predicate)
                .fetch();

        return allJobs.stream()
                .filter(j -> RangeFilter.matchesFilter(j.getSalary(), salary))
                .filter(j -> RangeFilter.matchesFilter(j.getExperience(), experience))
                .count();
    }

    private Page<JobWithRecruiterDTO> getJobsWithStandardFiltering(
            JobType type,
            JobStatus status,
            String salary,
            String experience,
            String recruiterId,
            List<String> fieldDetailIds,
            Pageable pageable,
            String sortBy,
            Sort.Direction direction
    ) {
        QJob job = QJob.job;
        QJobField jobField = QJobField.jobField;

        BooleanBuilder predicate = new BooleanBuilder();
        applyCommonFilters(predicate, job, jobField, type, status, recruiterId, fieldDetailIds);

        List<Job> allJobs = queryFactory.selectFrom(job)
                .distinct()
                .leftJoin(job.jobFields, jobField)
                .where(predicate)
                .orderBy(getOrderSpecifier(sortBy, direction))
                .fetch();

        List<Job> filteredJobs = allJobs.stream()
                .filter(j -> RangeFilter.matchesFilter(j.getSalary(), salary))
                .filter(j -> RangeFilter.matchesFilter(j.getExperience(), experience))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredJobs.size());

        List<Job> pageContent = start >= filteredJobs.size() ? new ArrayList<>() : filteredJobs.subList(start, end);

        return convertJobsToDTO(pageContent, pageable, filteredJobs.size());
    }

    public Page<JobWithRecruiterDTO> getPaginatedJobs(
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
        Pageable pageable = PageRequest.of(page - 1, size);

        if (query == null || query.trim().isEmpty()) {
            return getJobsWithStandardFiltering(type, status, salary, experience, recruiterId, fieldDetailIds, pageable, sortBy, direction);
        }

        List<Job> jobsByName = findJobsByNameQuery(query, type, status, salary, experience, recruiterId, fieldDetailIds, size, sortBy, direction);

        if (jobsByName.size() >= size) {
            List<Job> limitedJobs = jobsByName.subList(0, size);
            long totalCount = countJobsByNameQuery(query, type, status, salary, experience, recruiterId, fieldDetailIds);

            return convertJobsToDTO(limitedJobs, pageable, totalCount);
        }

        int remainingSize = size - jobsByName.size();
        List<Job> jobsByRecruiterName = findJobsByRecruiterNameQuery(
                query,
                type,
                status,
                salary,
                experience,
                recruiterId,
                fieldDetailIds,
                remainingSize,
                sortBy,
                direction,
                jobsByName
        );

        List<Job> combinedJobs = new ArrayList<>(jobsByName);
        combinedJobs.addAll(jobsByRecruiterName);

        long totalJobsByName = countJobsByNameQuery(query, type, status, salary, experience, recruiterId, fieldDetailIds);
        long totalJobsByRecruiter = countJobsByRecruiterNameQuery(query, type, status, salary, experience, recruiterId, fieldDetailIds, jobsByName);
        long totalCount = totalJobsByName + totalJobsByRecruiter;

        return convertJobsToDTO(combinedJobs, pageable, totalCount);
    }

    public JobDTO getJobById(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm"));

        return jobMapper.toDto(job);
    }

    public List<JobDTO> getJobsByIds(List<String> ids) {
        List<Job> jobs = jobRepository.findAllById(ids);
        return jobs.stream().map(jobMapper::toDto).toList();
    }

    public JobDTO createJob(CreateJobRequest createJobRequest, String recruiterId) {
        try {
            if (!recruiterServiceClient.checkIfRecruiterExists(recruiterId)) {
                throw new ResourceNotFoundException("Không tìm thấy nhà tuyển dụng");
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm"));

        String oldName = job.getName();
        jobMapper.updateJobFromRequest(updateJobRequest, job);

        if (updateJobRequest.getFieldDetails() != null) {
            updateJobFields(job, updateJobRequest.getFieldDetails());
        }

        if (!oldName.equals(job.getName())) {
            JobUpdateEvent event = new JobUpdateEvent(jobId, job.getName());
            jobUpdateKafkaTemplate.send("job-update-events", event);
        }

        Job updatedJob = jobRepository.save(job);
        return jobMapper.toDto(updatedJob);
    }

    private void updateJobFields(Job job, String[] fieldDetailIds) {
        List<JobField> existingJobFields = jobFieldRepository.findByJobId(job.getId());

        Set<String> existingFieldDetailIds = existingJobFields.stream()
                .map(jf -> jf.getFieldDetail().getId())
                .collect(Collectors.toSet());
        Set<String> newFieldDetailIds = new HashSet<>(Arrays.asList(fieldDetailIds));

        existingJobFields.stream()
                .filter(jf -> !newFieldDetailIds.contains(jf.getFieldDetail().getId()))
                .forEach(jobFieldRepository::delete);

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm"));

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
