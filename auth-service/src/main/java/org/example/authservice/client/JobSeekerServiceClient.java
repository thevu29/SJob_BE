package org.example.authservice.client;

import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.authservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "job-seeker-service", url = "${service.job-seeker.url}", path = "/api/job-seekers", configuration = FeignClientInterceptor.class)
public interface JobSeekerServiceClient {
    @PostMapping("/email-or-create")
    ApiResponse<JobSeekerWithUserDTO> getOrCreateJobSeekerByEmail(@RequestBody JobSeekerCreationDTO request);

    @GetMapping("/email/{email}")
    ApiResponse<JobSeekerWithUserDTO> getJobSeekerByEmail(@PathVariable String email);

    @PostMapping
    ApiResponse<JobSeekerWithUserDTO> createJobSeeker(@RequestBody JobSeekerCreationDTO request);
}
