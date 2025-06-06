package com.example.jobservice.controller;

import com.example.jobservice.dto.Job.JobStatisticsDTO;
import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.JobType;
import com.example.jobservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.Job.JobStatus;
import org.example.common.dto.Job.JobWithRecruiterDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import com.example.jobservice.dto.Job.GetTopRecruiterDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping("/top-recruiters")
    public ResponseEntity<ApiResponse<List<GetTopRecruiterDTO>>> getTopRecruitersWithMostJobs() {
        List<GetTopRecruiterDTO> topRecruiters = jobService.getTopRecruitersWithMostJobs();
        return ResponseEntity.ok(ApiResponse.success(topRecruiters, "Lấy danh sách nhà tuyển dụng hàng đầu thành công"));
    }

    @GetMapping("count-in-month")
    public ResponseEntity<ApiResponse<JobStatisticsDTO>> getJobCountsInMonth() {
        JobStatisticsDTO count = jobService.getJobCountsInMonth();
        return ResponseEntity.ok(ApiResponse.success(count, "Lấy số lượng việc làm trong tháng thành công"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<JobStatisticsDTO>>> getJobStatistics() {
        List<JobStatisticsDTO> jobStats = jobService.getJobStatistics();
        return ResponseEntity.ok(ApiResponse.success(jobStats, "Lấy thống kê việc làm thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobWithRecruiterDTO>>> getPaginatedJobs(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "type", required = false) JobType type,
            @RequestParam(value = "status", required = false) JobStatus status,
            @RequestParam(value = "salary", required = false) String salary,
            @RequestParam(value = "experience", required = false) String experience,
            @RequestParam(value = "recruiterId", required = false) String recruiterId,
            @RequestParam(value = "fieldDetailIds", required = false) List<String> fieldDetailIds,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<JobWithRecruiterDTO> pages = jobService.getPaginatedJobs(
                query,
                type,
                status,
                salary,
                experience,
                recruiterId,
                fieldDetailIds,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách các việc làm thành công"));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobsByIds(@RequestParam List<String> ids) {
        List<JobDTO> jobs = jobService.getJobsByIds(ids);
        return ResponseEntity.ok(
                ApiResponse.success(jobs, "Lấy danh sách việc làm thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/suggest-job-seekers/{jobId}")
    public ResponseEntity<ApiResponse<List<JobSeekerWithUserDTO>>> suggestJobSeekers(@PathVariable String jobId) {
        List<JobSeekerWithUserDTO> suggestedSeekers = jobService.suggestJobSeekers(jobId);
        return ResponseEntity.ok(
                ApiResponse.success(suggestedSeekers, "Gợi ý ứng viên thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/suggest-jobs/{jobSeekerId}")
    public ResponseEntity<ApiResponse<List<JobWithRecruiterDTO>>> suggestJobs(@PathVariable String jobSeekerId) {
        List<JobWithRecruiterDTO> suggestedJobs = jobService.suggestJobs(jobSeekerId);
        return ResponseEntity.ok(
                ApiResponse.success(suggestedJobs, "Gợi ý việc làm thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/recruiters/{recruiterId}")
    public ResponseEntity<ApiResponse<JobDTO>> createJob(@Valid @RequestBody CreateJobRequest createJobRequest, @PathVariable String recruiterId) {
        JobDTO job = jobService.createJob(createJobRequest, recruiterId);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(ApiResponse.success(job, "Tạo việc làm thành công", HttpStatus.CREATED));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobWithRecruiterDTO>> getJobById(@PathVariable String id) {
        JobWithRecruiterDTO job = jobService.getJobById(id);
        return ResponseEntity.ok(
                ApiResponse.success(job, "Lấy chi tiết việc làm thành công", HttpStatus.OK)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDTO>> updateJob(@Valid @RequestBody UpdateJobRequest updateJobRequest, @PathVariable String id) {
        JobDTO job = jobService.updateJob(updateJobRequest, id);
        return ResponseEntity.ok(
                ApiResponse.success(job, "Cập nhật việc làm thành công", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa việc làm thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<?>> importFile(@RequestParam("file") MultipartFile file) {
        jobService.importFileCSV(file);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Nhập file thành công", HttpStatus.OK)
        );
    }
}
