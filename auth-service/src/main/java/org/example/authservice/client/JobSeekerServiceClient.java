package org.example.authservice.client;

import org.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.example.authservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "job-seeker-service", url = "${service.job-seeker.url}", path = "/api/job-seekers", configuration = FeignClientInterceptor.class)
public interface JobSeekerServiceClient {
    @PostMapping
    ApiResponse<JobSeekerWithUserDTO> createJobSeeker(@RequestBody JobSeekerCreationDTO request);
}
