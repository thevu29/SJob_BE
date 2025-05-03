package com.example.recruiterservice.client;

import com.example.recruiterservice.config.FeignClientInterceptor;
import org.common.dto.User.UserCreationDTO;
import org.common.dto.User.UserDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${service.user.url}", path = "/api/users", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {
    @PostMapping
    ApiResponse<UserDTO> createUser(@RequestBody UserCreationDTO request);

    @GetMapping
    ApiResponse<List<UserDTO>> findPagedUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "role", defaultValue = "RECRUITER") String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    );

    @GetMapping("/ids")
    ApiResponse<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<String> ids);

    @GetMapping("/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") String id);

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") String id);
}
