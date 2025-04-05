package org.example.jobseekerservice.client;

import org.example.jobseekerservice.dto.JobSeeker.UserDTO;
import org.example.jobseekerservice.dto.JobSeeker.request.CreateUserRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") String id);

    @GetMapping("/api/users/ids")
    ApiResponse<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<String> ids);

    @GetMapping("/api/users")
    ApiResponse<List<UserDTO>> findPagedUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "role", defaultValue = "JOB_SEEKER") String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    );

    @PostMapping("/api/users")
    ApiResponse<UserDTO> createUser(@RequestBody CreateUserRequest request);

    @DeleteMapping("/api/users/{id}")
    void deleteUser(@PathVariable("id") String id);
}
