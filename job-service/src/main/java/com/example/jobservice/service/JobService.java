package com.example.jobservice.service;

import com.example.jobservice.client.RecruiterServiceClient;
import com.example.jobservice.dto.Job.JobDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.Job;
import com.example.jobservice.entity.JobStatus;
import com.example.jobservice.exception.ResourceNotFoundException;
import com.example.jobservice.mapper.JobMapper;
import com.example.jobservice.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final RecruiterServiceClient recruiterServiceClient;
    private final JobMapper jobMapper;

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
            return jobMapper.toDto(savedJob);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create job", e);
        }
    }

    public JobDTO updateJob(UpdateJobRequest updateJobRequest, String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id:" + jobId));

        jobMapper.updateJobFromRequest(updateJobRequest, job);
        Job updatedJob = jobRepository.save(job);
        return jobMapper.toDto(updatedJob);
    }

    public void deleteJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id:" + jobId));

        jobRepository.delete(job);
    }
}
