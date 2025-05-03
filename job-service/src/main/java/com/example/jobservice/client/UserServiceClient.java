package com.example.jobservice.client;


import com.example.jobservice.config.FeignClientInterceptor;

import org.common.dto.Recruiter.RecruiterWithUserDTO;
import org.common.dto.User.UserDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "user-service", url = "${service.user.url}", path = "/api/users", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {
    @GetMapping("/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable String id);
}
