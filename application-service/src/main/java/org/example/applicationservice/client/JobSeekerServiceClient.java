package org.example.applicationservice.client;

import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.applicationservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "job-seeker-service", url = "${service.job-seeker.url}", path = "/api/job-seekers", configuration = FeignClientInterceptor.class)
public interface JobSeekerServiceClient {
    @GetMapping("/{id}")
    ApiResponse<JobSeekerWithUserDTO> getJobSeekerById(@PathVariable String id);
}
