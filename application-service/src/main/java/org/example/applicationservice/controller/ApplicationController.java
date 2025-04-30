package org.example.applicationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.common.dto.Application.ApplicationDTO;
import org.common.dto.response.ApiResponse;
import org.example.applicationservice.dto.ApplicationCreationDTO;
import org.example.applicationservice.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationDTO>> createApplication(@Valid @ModelAttribute ApplicationCreationDTO request) {
        ApplicationDTO application = applicationService.createApplication(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(application, "Ứng tuyển thành công", HttpStatus.CREATED));
    }
}
