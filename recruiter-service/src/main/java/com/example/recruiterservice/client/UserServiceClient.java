package com.example.recruiterservice.client;

import com.example.recruiterservice.dto.UserDTO;
import com.example.recruiterservice.dto.request.CreateUserRequest;
import com.example.recruiterservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/api/users")
    ApiResponse<UserDTO> createUser(@RequestBody CreateUserRequest request);

    @GetMapping("/api/users/ids")
    ApiResponse<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<String> ids);

    @GetMapping("/api/users/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") String id);

    @DeleteMapping("/api/users/hard/{id}")
    void deleteUser(@PathVariable("id") String id);
}
