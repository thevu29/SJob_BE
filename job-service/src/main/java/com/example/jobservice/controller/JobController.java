package com.example.jobservice.controller;

import com.example.jobservice.dto.Job.request.CreateJobRequest;
import com.example.jobservice.dto.Job.request.UpdateJobRequest;
import com.example.jobservice.entity.JobType;
import com.example.jobservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.Job.JobStatus;
import org.example.common.dto.Job.JobWithRecruiterDTO;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobs(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "type", required = false) JobType type,
            @RequestParam(value = "status", required = false) JobStatus status,
            @RequestParam(value = "recruiterId", required = false) String recruiterId,
            @RequestParam(value = "fieldDetailIds", required = false) List<String> fieldDetailIds,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<JobDTO> pages = jobService.findPaginatedJobs(
                query,
                type,
                status,
                recruiterId,
                fieldDetailIds,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách các việc làm thành công"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<JobWithRecruiterDTO>>> getAllJobs() {
        List<JobWithRecruiterDTO> jobDTOList = jobService.getJobs();
        return ResponseEntity.ok(
                ApiResponse.success(jobDTOList, "Lấy danh sách các việc làm thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobsByIds(@RequestParam List<String> ids) {
        List<JobDTO> jobs = jobService.getJobsByIds(ids);
        return ResponseEntity.ok(
                ApiResponse.success(jobs, "Lấy danh sách việc làm thành công", HttpStatus.OK)
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
    public ResponseEntity<ApiResponse<JobDTO>> getJob(@PathVariable String id) {
        JobDTO job = jobService.getJob(id);
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
