package com.example.jobservice.controller;

import com.example.jobservice.dto.Job.JobDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.dto.response.ApiResponse;
import com.example.jobservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDTO>>> getAllJobs() {
        List<JobDTO> jobDTOList = jobService.getJobs();
        return ResponseEntity.ok(
                ApiResponse.success(jobDTOList, "Jobs fetched successfully", HttpStatus.OK)
        );
    }

    @PostMapping("/recruiters/{recruiterId}")
    public ResponseEntity<ApiResponse<JobDTO>> createJob(@Valid @RequestBody CreateJobRequest createJobRequest, @PathVariable String recruiterId) {
        JobDTO job = jobService.createJob(createJobRequest, recruiterId);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(ApiResponse.success(job, "Job created successfully", HttpStatus.CREATED));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDTO>> getJob(@PathVariable String id) {
        JobDTO job = jobService.getJob(id);
        return ResponseEntity.ok(
                ApiResponse.success(job, "Job fetched successfully", HttpStatus.OK)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDTO>> updateJob(@Valid @RequestBody UpdateJobRequest updateJobRequest, @PathVariable String id) {
        JobDTO job = jobService.updateJob(updateJobRequest, id);
        return ResponseEntity.ok(
                ApiResponse.success(job, "Job updated successfully", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(
                ApiResponse.success(null,"Job deleted successfully", HttpStatus.OK)
        );
    }

}
