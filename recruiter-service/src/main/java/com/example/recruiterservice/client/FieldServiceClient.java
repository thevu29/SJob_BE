package com.example.recruiterservice.client;

import com.example.recruiterservice.config.FeignClientInterceptor;
import com.example.recruiterservice.dto.FieldDTO;
import jakarta.validation.Valid;
import org.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.common.dto.NotificationPreference.NotificationPreferenceDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "job-service-fields", url = "${service.job.url}", path = "/api/fields", configuration = FeignClientInterceptor.class)
public interface FieldServiceClient {
    @GetMapping("/{id}")
    ApiResponse<FieldDTO> getField(@PathVariable String id);

    @PostMapping("/by-names")
    ApiResponse<List<FieldDTO>> getFieldsByNames(@RequestBody List<String> names);
}
