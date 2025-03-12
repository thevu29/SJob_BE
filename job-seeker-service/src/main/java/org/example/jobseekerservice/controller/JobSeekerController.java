package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.JobSeekerWithUserDTO;
import org.example.jobseekerservice.dto.request.CreateJobSeekerRequest;
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

    @PostMapping
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> createJobSeeker(@Valid @RequestBody CreateJobSeekerRequest request) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.createJobSeeker(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobSeeker, "Job Seeker created successfully", HttpStatus.CREATED));
    }
}
