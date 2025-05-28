package org.example.authservice.client;

import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.authservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "recruiter-service", url = "${service.recruiter.url}", path = "/api/recruiters", configuration = FeignClientInterceptor.class)
public interface RecruiterServiceClient {
    @GetMapping("/email/{email}")
    ApiResponse<RecruiterWithUserDTO> getRecruiterByEmail(@PathVariable String email);

    @PostMapping
    ApiResponse<RecruiterWithUserDTO> createRecruiter(@RequestBody RecruiterCreationDTO request);
}
