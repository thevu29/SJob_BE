package org.example.applicationservice.client;

import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.applicationservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "recruiter-service", url = "${service.recruiter.url}", path = "/api/recruiters", configuration = FeignClientInterceptor.class)
public interface RecruiterServiceClient {
    @GetMapping("/{id}")
    ApiResponse<RecruiterWithUserDTO> getRecruiterById(@PathVariable String id);
}
