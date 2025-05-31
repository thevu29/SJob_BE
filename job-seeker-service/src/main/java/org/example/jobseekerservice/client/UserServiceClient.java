package org.example.jobseekerservice.client;

import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.jobseekerservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${service.user.url}", path = "/api/users", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {
    @PostMapping("/email-or-create")
    ApiResponse<UserDTO> getOrCreateUserByEmail(@RequestBody UserCreationDTO request);

    @GetMapping("/email/{email}")
    ApiResponse<UserDTO> getUserByEmail(@PathVariable String email);

    @GetMapping("/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable String id);

    @GetMapping("/ids")
    ApiResponse<List<UserDTO>> getUsersByIds(@RequestParam List<String> ids);

    @GetMapping
    ApiResponse<List<UserDTO>> findPagedUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "role", defaultValue = "JOB_SEEKER") String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    );

    @PostMapping
    ApiResponse<UserDTO> createUser(@RequestBody UserCreationDTO request);

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable String id);
}
