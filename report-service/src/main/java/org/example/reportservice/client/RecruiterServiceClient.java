package org.example.reportservice.client;

import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.reportservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recruiter-service", url = "${service.recruiter.url}", path = "/api/recruiters", configuration = FeignClientInterceptor.class)
public interface RecruiterServiceClient {
    @GetMapping("/{id}")
    ApiResponse<RecruiterWithUserDTO> getRecruiterById(@PathVariable String id);

    @GetMapping
    ApiResponse<List<RecruiterWithUserDTO>> getRecruiters(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );
}
