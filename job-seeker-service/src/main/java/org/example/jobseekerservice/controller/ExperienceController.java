package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.ApiResponse;
import org.example.jobseekerservice.dto.Experience.ExperienceCreationDTO;
import org.example.jobseekerservice.dto.Experience.ExperienceDTO;
import org.example.jobseekerservice.dto.Experience.ExperienceUpdateDTO;
import org.example.jobseekerservice.service.ExperienceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {
    private final ExperienceService experienceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceDTO>>> getAllExperiences() {
        List<ExperienceDTO> experiences = experienceService.getAllExperiences();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(experiences, "Experiences fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/job-seeker/{jobSeekerId}")
    public ResponseEntity<ApiResponse<List<ExperienceDTO>>> getJobSeekerExperiences(@PathVariable String jobSeekerId) {
        List<ExperienceDTO> experiences = experienceService.getJobSeekerExperiences(jobSeekerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(experiences, "Experiences fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceDTO>> getExperienceById(@PathVariable String id) {
        ExperienceDTO experience = experienceService.getExperienceById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(experience, "Experience fetched successfully", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceDTO>> createExperience(@Valid @RequestBody ExperienceCreationDTO request) {
        ExperienceDTO createdExperience = experienceService.createExperience(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdExperience, "Experience created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceDTO>> updateExperience(
            @PathVariable String id,
            @Valid @RequestBody ExperienceUpdateDTO request
    ) {
        ExperienceDTO updatedExperience = experienceService.updateExperience(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedExperience, "Experience updated successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(@PathVariable String id) {
        experienceService.deleteExperience(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Experience deleted successfully", HttpStatus.NO_CONTENT));
    }
}
