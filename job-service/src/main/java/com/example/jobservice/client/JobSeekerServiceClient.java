package com.example.jobservice.client;

import com.example.jobservice.config.FeignClientInterceptor;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "job-seeker-service", url = "${service.job-seeker.url}", path = "/api/job-seekers", configuration = FeignClientInterceptor.class)
public interface JobSeekerServiceClient {
    @GetMapping("/{id}")
    ApiResponse<JobSeekerWithUserDTO> getJobSeekerById(@PathVariable String id);

    @GetMapping("/all")
    ApiResponse<List<JobSeekerWithUserDTO>> getAllJobSeekers();
}
