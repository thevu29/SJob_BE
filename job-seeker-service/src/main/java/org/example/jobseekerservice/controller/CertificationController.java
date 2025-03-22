package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.Certifitcaion.CertificationDTO;
import org.example.jobseekerservice.dto.Certifitcaion.request.CreateCertificationRequest;
import org.example.jobseekerservice.dto.Certifitcaion.request.UpdateCertificationRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.service.CertificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {
    private final CertificationService certificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CertificationDTO>>> getCertifications() {
        List<CertificationDTO> certifications = certificationService.getCertifications();

        return ResponseEntity.ok(ApiResponse
                .success(certifications, "Certifications fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/job-seeker/{jobSeekerId}")
    public ResponseEntity<ApiResponse<List<CertificationDTO>>> getJobSeekerCertifications(@PathVariable String jobSeekerId) {
        List<CertificationDTO> certifications = certificationService.getJobSeekerCertifications(jobSeekerId);

        return ResponseEntity.ok(ApiResponse
                .success(certifications, "Certifications fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationDTO>> getCertificationById(@PathVariable String id) {
        CertificationDTO certification = certificationService.getCertificationById(id);

        return ResponseEntity.ok(ApiResponse
                .success(certification, "Certification fetched successfully", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CertificationDTO>> createCertification(
            @Valid @ModelAttribute CreateCertificationRequest request) {

        CertificationDTO createdCertification = certificationService.createCertification(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse
                        .success(createdCertification, "Certification created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationDTO>> updateCertification(
            @PathVariable String id,
            @Valid @ModelAttribute UpdateCertificationRequest request) {

        CertificationDTO updatedCertification = certificationService.updateCertification(id, request);

        return ResponseEntity.ok(ApiResponse
                .success(updatedCertification, "Certification updated successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCertification(@PathVariable String id) {
        certificationService.deleteCertification(id);

        return ResponseEntity.ok(ApiResponse
                .success("Certification deleted successfully", "Certification deleted successfully", HttpStatus.OK));
    }
}
