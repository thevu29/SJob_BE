package com.example.recruiterservice.controller;

import com.example.recruiterservice.dto.RecruiterWithUserDTO;
import com.example.recruiterservice.dto.request.CreateRecruiterRequest;
import com.example.recruiterservice.dto.response.ApiResponse;
import com.example.recruiterservice.service.RecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiters")
@RequiredArgsConstructor
public class RecruiterController {
    private final RecruiterService recruiterService;

    @GetMapping
    public String getRecruiters() {
        return "Recruiters fetched successfully";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> createRecruiter(@Valid @RequestBody CreateRecruiterRequest request) {
        RecruiterWithUserDTO recruiter = recruiterService.createRecruiter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(recruiter, "Recruiter created successfully", HttpStatus.CREATED));
    }
}
