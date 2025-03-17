package org.example.jobseekerservice.client;

import org.example.jobseekerservice.dto.UserDTO;
import org.example.jobseekerservice.dto.request.CreateUserRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") String id);

    @GetMapping("/api/users/ids")
    ApiResponse<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<String> ids);

    @PostMapping("/api/users")
    ApiResponse<UserDTO> createUser(@RequestBody CreateUserRequest request);

    @DeleteMapping("/api/users/{id}/hard")
    void deleteUser(@PathVariable("id") String id);
}
