package com.example.recruiterservice.client;

import com.example.recruiterservice.dto.FieldDTO;
import com.example.recruiterservice.dto.FieldDetailDTO;
import com.example.recruiterservice.dto.UserDTO;
import com.example.recruiterservice.dto.request.CreateUserRequest;
import com.example.recruiterservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
