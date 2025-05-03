package com.example.recruiterservice.client;

import com.example.recruiterservice.config.FeignClientInterceptor;
import org.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "job-seeker-service", url = "${service.jobSeeker.url}", path = "/api/job-seekers", configuration = FeignClientInterceptor.class)
public interface JobSeekerServiceClient {
    @GetMapping("/{id}")
    ApiResponse<JobSeekerWithUserDTO> getJobSeekerById(@PathVariable String id);
}
