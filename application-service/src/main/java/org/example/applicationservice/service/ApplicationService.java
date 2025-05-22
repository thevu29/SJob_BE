package org.example.applicationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.applicationservice.client.JobSeekerServiceClient;
import org.example.applicationservice.client.JobServiceClient;
import org.example.applicationservice.client.RecruiterServiceClient;
import org.example.applicationservice.client.ResumeServiceClient;
import org.example.applicationservice.dto.ApplicationCreationDTO;
import org.example.applicationservice.entity.Application;
import org.example.applicationservice.mapper.ApplicationMapper;
import org.example.applicationservice.repository.ApplicationRepository;
import org.example.common.dto.Application.ApplicationDTO;
import org.example.common.dto.Email.EmailMessageDTO;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.Resume.ResumeDTO;
import org.example.common.dto.S3.FileUploadedDTO;
import org.example.common.dto.S3.UploadFileDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final JobServiceClient jobServiceClient;
    private final ApplicationMapper applicationMapper;
    private final ResumeServiceClient resumeServiceClient;
    private final ApplicationRepository applicationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;

    private void validateResumeSelection(ApplicationCreationDTO request) {
        if (request.getResumeId() != null && !request.getResumeId().isBlank() && request.getResumeFile() != null) {
            throw new IllegalArgumentException("Chỉ có thể chọn một trong hai: CV có sẵn hoặc CV tải lên mới.");
        }
        if ((request.getResumeId() == null || request.getResumeId().isBlank()) && request.getResumeFile() == null) {
            throw new IllegalArgumentException("Vui lòng chọn CV để nộp.");
        }
    }

    private void attachExistingResume(Application application, String resumeId) {
        ApiResponse<ResumeDTO> resumeResponse = resumeServiceClient.getResumeById(resumeId);
        ResumeDTO resume = resumeResponse.getData();
        if (resume != null) {
            application.setResumeUrl(resume.getUrl());
        }
    }

    private void sendUploadFileMessage(String id, MultipartFile resumeFile) {
        try {
            UploadFileDTO uploadFileDTO = UploadFileDTO.builder()
                    .id(id)
                    .fileContent(resumeFile.getBytes())
                    .fileName(resumeFile.getOriginalFilename())
                    .contentType(resumeFile.getContentType())
                    .build();

            kafkaTemplate.send("upload-file", uploadFileDTO).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Kafka message for file upload", e);
        }
    }

    private void sendEmail(
            String jobSeekerEmail,
            String recruiterEmail,
            String jobTitle,
            String applicationMessage,
            String fileUrl
    ) {
        EmailMessageDTO jobSeekerEmailMessage = EmailMessageDTO.builder()
                .to(jobSeekerEmail)
                .subject("Ứng tuyển thành công")
                .body("Chúc mừng bạn đã ứng tuyển thành công cho vị trí " + jobTitle)
                .fileUrl(fileUrl)
                .build();

        EmailMessageDTO recruiterEmailMessage = EmailMessageDTO.builder()
                .to(recruiterEmail)
                .subject("Ứng viên mới ứng tuyển")
                .body("Có một ứng viên mới đã ứng tuyển cho vị trí " + jobTitle + "<br>Nội dung ứng tuyển: " + applicationMessage)
                .fileUrl(fileUrl)
                .build();

        kafkaTemplate.send("send-email-with-attachment", jobSeekerEmailMessage);
        kafkaTemplate.send("send-email-with-attachment", recruiterEmailMessage);
    }

    private void updateApplicationResume(String id, String resumeUrl) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển"));

        application.setResumeUrl(resumeUrl);
        applicationRepository.save(application);
    }

    @Transactional
    public ApplicationDTO createApplication(ApplicationCreationDTO request) {
        validateResumeSelection(request);

        ApiResponse<JobDTO> jobResponse = jobServiceClient.getJobById(request.getJobId());
        ApiResponse<JobSeekerWithUserDTO> jobSeekerResponse = jobSeekerServiceClient.getJobSeekerById(request.getJobSeekerId());
        ApiResponse<RecruiterWithUserDTO> recruiterResponse = recruiterServiceClient.getRecruiterById(jobResponse.getData().getRecruiterId());

        Application application = applicationMapper.toEntity(request);
        application.setJobId(jobResponse.getData().getId());
        application.setJobSeekerId(jobSeekerResponse.getData().getId());

        if (request.getResumeId() != null && !request.getResumeId().isBlank()) {
            attachExistingResume(application, request.getResumeId());
            sendEmail(
                    jobSeekerResponse.getData().getEmail(),
                    recruiterResponse.getData().getEmail(),
                    jobResponse.getData().getName(),
                    request.getMessage(),
                    application.getResumeUrl()
            );
        }

        Application createdApplication = applicationRepository.save(application);

        if (request.getResumeFile() != null) {
            sendUploadFileMessage(createdApplication.getId(), request.getResumeFile());
        }

        return applicationMapper.toDTO(createdApplication);
    }

    public ApplicationDTO getApplicationById(String id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển"));

        return applicationMapper.toDTO(application);
    }

    @KafkaListener(topics = "file-uploaded", groupId = "application-service")
    public void handleFileUploaded(FileUploadedDTO event) {
        updateApplicationResume(event.getId(), event.getFileUrl());

        ApplicationDTO application = getApplicationById(event.getId());

        ApiResponse<JobDTO> jobResponse = jobServiceClient.getJobById(application.getJobId());
        ApiResponse<JobSeekerWithUserDTO> jobSeekerResponse = jobSeekerServiceClient.getJobSeekerById(application.getJobSeekerId());
        ApiResponse<RecruiterWithUserDTO> recruiterResponse = recruiterServiceClient.getRecruiterById(jobResponse.getData().getRecruiterId());

        EmailMessageDTO jobSeekerEmailMessage = EmailMessageDTO.builder()
                .to(jobSeekerResponse.getData().getEmail())
                .subject("Ứng tuyển thành công")
                .body("Chúc mừng bạn đã ứng tuyển thành công cho vị trí " + jobResponse.getData().getName())
                .fileUrl(event.getFileUrl())
                .build();

        EmailMessageDTO recruiterEmailMessage = EmailMessageDTO.builder()
                .to(recruiterResponse.getData().getEmail())
                .subject("Ứng viên mới ứng tuyển")
                .body("Có một ứng viên mới đã ứng tuyển cho vị trí " + jobResponse.getData().getName() + "<br>Nội dung ứng tuyển: " + application.getMessage())
                .fileUrl(event.getFileUrl())
                .build();

        kafkaTemplate.send("send-email-with-attachment", jobSeekerEmailMessage);
        kafkaTemplate.send("send-email-with-attachment", recruiterEmailMessage);
    }

    public Page<ApplicationDTO> getPaginatedJobSeekerApplications(
            String jobSeekerId,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Application> applicationPage = applicationRepository.findAllByJobSeekerId(jobSeekerId, pageable);

        List<ApplicationDTO> content = new ArrayList<>();

        applicationPage.forEach(application -> {
            ApplicationDTO applicationDTO = applicationMapper.toDTO(application);

            ApiResponse<JobDTO> jobResponse = jobServiceClient.getJobById(application.getJobId());

            if (jobResponse.getData() != null) {
                applicationDTO.setJob(jobResponse.getData());
            }

            content.add(applicationDTO);
        });

        return new PageImpl<>(content, pageable, applicationPage.getTotalElements());
    }
}
