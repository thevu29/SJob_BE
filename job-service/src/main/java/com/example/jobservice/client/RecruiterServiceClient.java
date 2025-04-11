package com.example.jobservice.client;


import com.example.jobservice.dto.Recruiter.RecruiterDTO;
import com.example.jobservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "recruiter-service")
public interface RecruiterServiceClient {
    @GetMapping("/api/recruiters/exists/{id}")
    boolean checkIfRecruiterExists(@PathVariable String id);

    @PostMapping("/api/recruiters/by-names")
    ApiResponse<List<RecruiterDTO>> getRecruiterByName(@RequestBody List<String> names);
}
