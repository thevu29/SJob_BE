package org.example.applicationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.applicationservice.dto.ViewedJobCreationDTO;
import org.example.applicationservice.dto.ViewedJobDTO;
import org.example.applicationservice.service.ViewedJobService;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viewed-jobs")
@RequiredArgsConstructor
public class ViewedJobController {
    private final ViewedJobService viewedJobService;

    @PostMapping
    public ResponseEntity<ApiResponse<ViewedJobDTO>> viewJob(@Valid @RequestBody ViewedJobCreationDTO request) {
        ViewedJobDTO viewedJob = viewedJobService.viewJob(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(viewedJob, "Xem việc làm thành công", HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ViewedJobDTO>>> getPaginatedViewedJobs(
            @RequestParam(value = "jobSeekerId", required = false) String jobSeekerId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<ViewedJobDTO> pages = viewedJobService.getPaginatedViewedJobs(jobSeekerId, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách các việc làm đã xem thành công"));
    }
}
