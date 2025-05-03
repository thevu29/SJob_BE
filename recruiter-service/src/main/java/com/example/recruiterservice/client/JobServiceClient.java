package com.example.recruiterservice.client;

import com.example.recruiterservice.config.FeignClientInterceptor;
import org.common.dto.Field.FieldDTO;
import org.common.dto.Job.JobDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "job-service", url = "${service.job.url}", path = "/api/jobs", configuration = FeignClientInterceptor.class)
public interface JobServiceClient {
    @GetMapping("/{id}")
    ApiResponse<JobDTO> getJob(@PathVariable String id);
}
