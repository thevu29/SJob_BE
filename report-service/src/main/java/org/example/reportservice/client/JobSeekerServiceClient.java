package org.example.reportservice.client;

import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.reportservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "job-seeker-service", url = "${service.jobSeeker.url}", path = "/api/job-seekers", configuration = FeignClientInterceptor.class)
public interface JobSeekerServiceClient {
    @GetMapping("/{id}")
    ApiResponse<JobSeekerWithUserDTO> getJobSeekerById(@PathVariable String id);

    @GetMapping
    ApiResponse<List<JobSeekerWithUserDTO>> getJobSeekers(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size
    );
}
