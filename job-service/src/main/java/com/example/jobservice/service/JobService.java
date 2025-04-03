package com.example.jobservice.service;

import com.example.jobservice.client.RecruiterServiceClient;
import com.example.jobservice.dto.FieldDetail.FieldDetailDTO;
import com.example.jobservice.dto.Job.JobDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.entity.Job;
import com.example.jobservice.entity.JobField;
import com.example.jobservice.entity.JobStatus;
import com.example.jobservice.exception.ResourceNotFoundException;
import com.example.jobservice.mapper.JobMapper;
import com.example.jobservice.repository.FieldDetailRepository;
import com.example.jobservice.repository.JobFieldRepository;
import com.example.jobservice.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final RecruiterServiceClient recruiterServiceClient;
    private final JobMapper jobMapper;
    private final JobFieldRepository jobFieldRepository;

    public List<JobDTO> getJobs() {
        List<Job> jobs = jobRepository.findAll();

        return jobs.stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    public JobDTO getJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id:" + jobId));
        return jobMapper.toDto(job);
    }

    public JobDTO createJob(CreateJobRequest createJobRequest,String recruiterId) {
        try{
            if(!recruiterServiceClient.checkIfRecruiterExists(recruiterId)){
                throw new ResourceNotFoundException("Recruiter not found with id:" + recruiterId);
            }
            Job job = jobMapper.toEntity(createJobRequest);
            job.setRecruiterId(recruiterId);
            job.setStatus(JobStatus.OPEN);
            job.setDate(LocalDate.now());
            job.setCloseWhenFull(false);
            Job savedJob = jobRepository.save(job);

            // Add job fields
            for(String fieldDetailId : createJobRequest.getFieldDetails()){
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
            throw new RuntimeException("Failed to create job", e);
        }
    }

    public JobDTO updateJob(UpdateJobRequest updateJobRequest, String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id:" + jobId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id:" + jobId));

        jobRepository.delete(job);
    }
}
