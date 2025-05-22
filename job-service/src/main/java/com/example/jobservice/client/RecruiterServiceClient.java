package com.example.jobservice.client;

import com.example.jobservice.config.FeignClientInterceptor;
import org.example.common.dto.Recruiter.RecruiterDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "recruiter-service", url = "${service.recruiter.url}", path = "/api/recruiters", configuration = FeignClientInterceptor.class)
public interface RecruiterServiceClient {
    @GetMapping("/exists/{id}")
    boolean checkIfRecruiterExists(@PathVariable String id);

    @PostMapping("/by-names")
    ApiResponse<List<RecruiterDTO>> getRecruiterByName(@RequestBody List<String> names);

    @GetMapping("/{id}")
    ApiResponse<RecruiterWithUserDTO> getRecruiterById(@PathVariable String id);

    @PostMapping("/ids")
    ApiResponse<List<RecruiterDTO>> getRecruiterByIds(@RequestBody List<String> ids);

    @GetMapping
    ApiResponse<List<RecruiterWithUserDTO>> getRecruiters(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    );
}
