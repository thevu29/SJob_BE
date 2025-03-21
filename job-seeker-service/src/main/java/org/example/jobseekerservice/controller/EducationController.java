package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.Education.EducationDTO;
import org.example.jobseekerservice.dto.Education.request.CreateEducationRequest;
import org.example.jobseekerservice.dto.Education.request.UpdateEducationRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.service.EducationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationDTO>>> getEducations() {
        List<EducationDTO> educations = educationService.getEducations();

        return ResponseEntity.ok(
                ApiResponse.success(educations, "Educations fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("job-seeker/{jobSeekerId}")
    public ResponseEntity<ApiResponse<List<EducationDTO>>> getJobSeekerEducations(@PathVariable String jobSeekerId) {
        List<EducationDTO> educations = educationService.getJobSeekerEducations(jobSeekerId);

        return ResponseEntity.ok(
                ApiResponse.success(educations, "Educations fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationDTO>> getEducationById(@PathVariable String id) {
        EducationDTO education = educationService.getEducationById(id);

        return ResponseEntity.ok(
                ApiResponse.success(education, "Education fetched successfully", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EducationDTO>> createEducation(@Valid @RequestBody CreateEducationRequest request) {
        EducationDTO education = educationService.createEducation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        education, "Education created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationDTO>> updateEducation(
            @PathVariable String id,
            @Valid @RequestBody UpdateEducationRequest request
    ) {
        EducationDTO education = educationService.updateEducation(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(education, "Education updated successfully", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteEducation(@PathVariable String id) {
        educationService.deleteEducation(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Education deleted successfully", HttpStatus.OK)
        );
    }
}
