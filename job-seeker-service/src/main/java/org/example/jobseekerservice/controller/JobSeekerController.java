package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.jobseekerservice.dto.JobSeeker.GetJobSeekerStatisticsDTO;
import org.example.jobseekerservice.dto.JobSeeker.JobSeekerUpdateDTO;
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

    @GetMapping("/count-in-month")
    public ResponseEntity<ApiResponse<GetJobSeekerStatisticsDTO>> getJobSeekerCountInMonth() {
        GetJobSeekerStatisticsDTO result = jobSeekerService.getJobSeekerCountInMonth();
        return ResponseEntity.ok(ApiResponse.success(result, "Lấy số lượng ứng viên trong tháng thành công"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<GetJobSeekerStatisticsDTO>>> getJobSeekerStatistics() {
        List<GetJobSeekerStatisticsDTO> stats = jobSeekerService.getJobSeekerStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy danh sách thống kê ứng viên thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobSeekerWithUserDTO>>> getJobSeekers(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "seeking", required = false) Boolean seeking,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<JobSeekerWithUserDTO> pages = jobSeekerService.findPagedJobSeekers(
                query,
                active,
                seeking,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách job seeker thành công"));
    }

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<JobSeekerWithUserDTO>>> getAllJobSeekers() {
        List<JobSeekerWithUserDTO> jobSeekers = jobSeekerService.getAllJobSeekers();
        return ResponseEntity.ok(
                ApiResponse.success(jobSeekers, "Lấy danh sách job seeker thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> getJobSeekerById(@PathVariable String id) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getJobSeekerById(id);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Lấy thông tin job seeker thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> getJobSeekerByEmail(@PathVariable String email) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getJobSeekerByEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Lấy thông tin job seeker thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/email-or-create")
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> getOrCreateJobSeekerByEmail(@Valid @RequestBody JobSeekerCreationDTO request) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getOrCreateJobSeeker(request);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Lấy hoặc tạo job seeker thành công", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> createJobSeeker(@Valid @RequestBody JobSeekerCreationDTO request) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.createJobSeeker(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(jobSeeker, "Tạo job seeker thành công", HttpStatus.CREATED));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<JobSeekerWithUserDTO>> updateJobSeeker(
            @PathVariable String id,
            @Valid @ModelAttribute JobSeekerUpdateDTO request
    ) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.updateJobSeeker(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(jobSeeker, "Update job seeker thành công", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteJobSeeker(@PathVariable String id) {
        jobSeekerService.deleteJobSeeker(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xoá job seeker thành công", HttpStatus.OK)
        );
    }
}
