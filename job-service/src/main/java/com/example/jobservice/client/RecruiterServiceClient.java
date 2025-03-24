package com.example.jobservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "recruiter-service")
public interface RecruiterServiceClient {
    @GetMapping("/api/recruiters/exists/{id}")
    boolean checkIfRecruiterExists(@PathVariable String id);
}
