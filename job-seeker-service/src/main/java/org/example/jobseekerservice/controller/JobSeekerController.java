package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.jobseekerservice.dto.JobSeeker.request.CreateJobSeekerRequest;
import org.example.jobseekerservice.dto.JobSeeker.request.UpdateJobSeekerRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.service.JobSeekerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-seekers")
@RequiredArgsConstructor
public class JobSeekerController {
    private final JobSeekerService jobSeekerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobSeekerWithUserDTO>>> getJobSeekers(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "seeking", required = false) Boolean seeking,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<JobSeekerWithUserDTO> pages = jobSeekerService.findPagedJobSeekers(
                query,
                active,
                seeking,
                page - 1,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Job Seekers fetched successfully"));
    }

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<JobSeekerWithUserDTO>>> getAllJobSeekers() {
        List<JobSeekerWithUserDTO> jobSeekers = jobSeekerService.getAllJobSeekers();
        return ResponseEntity.ok(
                ApiResponse.success(jobSeekers, "Job Seekers fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> getJobSeekerById(@PathVariable String id) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getJobSeekerById(id);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Job Seeker fetched successfully", HttpStatus.OK)
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
