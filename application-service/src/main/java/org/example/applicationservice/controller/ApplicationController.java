package org.example.applicationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.applicationservice.dto.ApplicationCreationDTO;
import org.example.applicationservice.dto.CheckJobSeekerApplyJobDTO;
import org.example.applicationservice.dto.GetApplicationStatisticsDTO;
import org.example.applicationservice.enums.ApplicationStatus;
import org.example.applicationservice.service.ApplicationService;
import org.example.common.dto.Application.ApplicationDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping("/job/job-seeker")
    public ResponseEntity<ApiResponse<ApplicationDTO>> getApplicationByJobIdAndJobSeekerId(
            @RequestParam("jobId") String jobId,
            @RequestParam("jobSeekerId") String jobSeekerId
    ) {
        ApplicationDTO application = applicationService.getApplicationByJobIdAndJobSeekerId(jobId, jobSeekerId);
        return ResponseEntity.ok(ApiResponse.success(application, "Lấy thông tin ứng tuyển thành công"));
    }

    @GetMapping("/count-in-month")
    public ResponseEntity<ApiResponse<GetApplicationStatisticsDTO>> getApplicationCountInMonth() {
        GetApplicationStatisticsDTO count = applicationService.getApplicationCountInMonth();
        return ResponseEntity.ok(ApiResponse.success(count, "Đếm số lượng ứng tuyển trong tháng thành công"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationDTO>> createApplication(@Valid @ModelAttribute ApplicationCreationDTO request) {
        ApplicationDTO application = applicationService.createApplication(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(application, "Ứng tuyển thành công", HttpStatus.CREATED));
    }

    @PostMapping("/check-apply")
    public ResponseEntity<ApiResponse<Boolean>> hasJobSeekerAppliedForJob(@Valid @RequestBody CheckJobSeekerApplyJobDTO request) {
        Boolean result = applicationService.hasJobSeekerAppliedForJob(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Kiểm tra ứng tuyển thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getPaginatedJobSeekerApplications(
            @RequestParam(value = "jobSeekerId", required = false) String jobSeekerId,
            @RequestParam(value = "jobId", required = false) String jobId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<ApplicationDTO> pages = applicationService.getPaginatedJobSeekerApplications(
                jobSeekerId,
                jobId,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách các công việc đã ừng tuyển thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDTO>> updateApplicationStatus(
            @PathVariable String id,
            @RequestBody String status
    ) {
        ApplicationDTO updatedApplication = applicationService.updateApplicationStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success(updatedApplication, "Cập nhật trạng thái đơn ứng tuyển thành công")
        );
    }
}
