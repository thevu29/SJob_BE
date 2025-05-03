package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.ApiResponse;
import org.example.jobseekerservice.dto.Resume.ResumeCreationDTO;
import org.example.jobseekerservice.dto.Resume.ResumeDTO;
import org.example.jobseekerservice.dto.Resume.ResumeUpdateDTO;
import org.example.jobseekerservice.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getAllResumes() {
        List<ResumeDTO> resumes = resumeService.getAllResumes();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(resumes, "Resumes fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/job-seeker/{jobSeekerId}")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getJobSeekerResumes(@PathVariable String jobSeekerId) {
        List<ResumeDTO> resumes = resumeService.getJobSeekerResumes(jobSeekerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(resumes, "Resumes fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeDTO>> getResumeById(@PathVariable String id) {
        ResumeDTO resume = resumeService.getResumeById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(resume, "Resume fetched successfully", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResumeDTO>> createResume(@Valid @ModelAttribute ResumeCreationDTO request) {
        ResumeDTO createdResume = resumeService.createResume(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdResume, "Resume created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeDTO>> updateResume(
            @PathVariable String id,
            @Valid @ModelAttribute ResumeUpdateDTO request
    ) {
        ResumeDTO updatedResume = resumeService.updateResume(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedResume, "Resume updated successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResume(@PathVariable String id) {
        resumeService.deleteResume(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Resume deleted successfully", HttpStatus.OK));
    }
}
