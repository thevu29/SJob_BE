package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.JobSeekerWithUserDTO;
import org.example.jobseekerservice.dto.request.CreateJobSeekerRequest;
import org.example.jobseekerservice.dto.request.UpdateJobSeekerRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.service.JobSeekerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-seekers")
@RequiredArgsConstructor
public class JobSeekerController {
    private final JobSeekerService jobSeekerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobSeekerWithUserDTO>>> getJobSeekers() {
        List<JobSeekerWithUserDTO> jobSeekers = jobSeekerService.getJobSeekers();
        return ResponseEntity.ok(
                ApiResponse.success(jobSeekers, "Job Seekers fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> getJobSeeker(@PathVariable String id) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getJobSeekerById(id);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Job Seeker fetched successfully", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> createJobSeeker(@Valid @ModelAttribute CreateJobSeekerRequest request) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.createJobSeeker(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobSeeker, "Job Seeker created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> updateJobSeeker(
            @PathVariable String id,
            @Valid @ModelAttribute UpdateJobSeekerRequest request
    ) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.updateJobSeeker(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Job Seeker updated successfully", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteJobSeeker(@PathVariable String id) {
        jobSeekerService.deleteJobSeeker(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Job Seeker deleted successfully", HttpStatus.OK)
        );
    }
}
