package org.example.applicationservice.client;

import org.common.dto.Resume.ResumeDTO;
import org.common.dto.response.ApiResponse;
import org.example.applicationservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resume-service", url = "${service.job-seeker.url}", path = "/api/resumes", configuration = FeignClientInterceptor.class)
public interface ResumeServiceClient {
    @GetMapping("/{id}")
    ApiResponse<ResumeDTO> getResumeById(@PathVariable String id);
}
