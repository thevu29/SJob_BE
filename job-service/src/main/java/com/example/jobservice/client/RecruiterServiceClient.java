package com.example.jobservice.client;


import com.example.jobservice.config.FeignClientInterceptor;
import org.common.dto.Recruiter.RecruiterDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "recruiter-service", url = "${service.recruiter.url}", path = "/api/recruiters", configuration = FeignClientInterceptor.class)
public interface RecruiterServiceClient {
    @GetMapping("/exists/{id}")
    boolean checkIfRecruiterExists(@PathVariable String id);

    @PostMapping("/by-names")
    ApiResponse<List<RecruiterDTO>> getRecruiterByName(@RequestBody List<String> names);
}
