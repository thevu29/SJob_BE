package com.example.recruiterservice.client;

import com.example.recruiterservice.dto.FieldDTO;
import com.example.recruiterservice.dto.FieldDetailDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "job-service")
public interface JobServiceClient {
    @GetMapping("/api/fields/{id}")
    ApiResponse<FieldDTO> getField(@PathVariable String id);

    @PostMapping("/api/fields/by-names")
    ApiResponse<List<FieldDTO>> getFieldsByNames(@RequestBody List<String> names);

    @PostMapping("/api/field-details/by-names")
    ApiResponse<List<FieldDetailDTO>> getFieldDetailsByNames(@RequestBody List<String> names);
}
