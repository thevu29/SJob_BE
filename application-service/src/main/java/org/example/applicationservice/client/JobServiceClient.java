package org.example.applicationservice.client;

import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.applicationservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "job-service", url = "${service.job.url}", path = "/api/jobs", configuration = FeignClientInterceptor.class)
public interface JobServiceClient {
    @GetMapping("/{id}")
    ApiResponse<JobDTO> getJobById(@PathVariable String id);

    @GetMapping("/ids")
    ApiResponse<List<JobDTO>> getJobByIds(@RequestParam List<String> ids);
}
