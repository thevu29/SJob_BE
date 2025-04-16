package org.example.authservice.client;

import org.common.dto.Recruiter.RecruiterCreationDTO;
import org.common.dto.Recruiter.RecruiterWithUserDTO;
import org.common.dto.response.ApiResponse;
import org.example.authservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "recruiter-service", url = "${service.recruiter.url}", path = "/api/recruiters", configuration = FeignClientInterceptor.class)
public interface RecruiterServiceClient {
    @PostMapping
    ApiResponse<RecruiterWithUserDTO> createRecruiter(@RequestBody RecruiterCreationDTO request);
}
