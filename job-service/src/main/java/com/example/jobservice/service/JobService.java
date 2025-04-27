package com.example.jobservice.service;

import com.example.jobservice.client.RecruiterServiceClient;
import com.example.jobservice.client.UserServiceClient;
import com.example.jobservice.dto.Job.JobDTO;
import com.example.jobservice.dto.Job.JobImportDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.entity.Job;
import com.example.jobservice.entity.JobField;
import com.example.jobservice.entity.JobStatus;
import com.example.jobservice.mapper.JobMapper;
import com.example.jobservice.repository.FieldDetailRepository;
import com.example.jobservice.repository.JobFieldRepository;
import com.example.jobservice.repository.JobRepository;
import com.example.jobservice.utils.helpers.CSVHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.common.dto.Email.EmailMessageDTO;
import org.common.dto.Notification.NotificationEvent;
import org.common.dto.Notification.NotificationRequestDTO;
import org.common.dto.Notification.NotificationType;
import org.common.dto.Recruiter.RecruiterDTO;
import org.common.dto.Recruiter.RecruiterWithUserDTO;
import org.common.dto.User.UserDTO;
import org.common.dto.response.ApiResponse;
import org.common.exception.FileUploadException;
import org.common.exception.ResourceNotFoundException;
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
    private final KafkaTemplate <String, NotificationRequestDTO> kafkaTemplate;
    ;
    private final CSVHelper csvHelper;

    public List<JobDTO> getJobs() {
        List<Job> jobs = jobRepository.findAll();

        return jobs.stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    public JobDTO getJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm với id:" + jobId));
        return jobMapper.toDto(job);
    }

    public void testKafak() {
        String userId = "67ffb4b8996bf14e1450c2d0";
        String email = "huyhoang191072@gmail.com";
        String title = "Thông báo hệ thống";
        String content = "Đây là thông báo hệ thống";
        NotificationRequestDTO notificationRequestDTO = NotificationEvent.systemAnnouncement(userId,email,title,content);
        kafkaTemplate.send("notification-requests", notificationRequestDTO);

//        EmailMessageDTO emailMessageDTO = EmailMessageDTO.builder()
//                .to("hoangdaden2003@gmail.com")
//                .subject("SMTP")
//                .body("Content")
//                .build();
//        kafkaTemplate.send("send-email", emailMessageDTO);
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
            System.out.println("Job: " + job);
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

        jobMapper.updateJobFromRequest(updateJobRequest, job);


        // Update field details if provided
        if (updateJobRequest.getFieldDetails() != null) {
            updateJobFields(job, updateJobRequest.getFieldDetails());
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
