package com.example.recruiterservice.client;

import com.example.recruiterservice.config.FeignClientInterceptor;
import org.common.dto.User.UserCreationDTO;
import org.common.dto.User.UserDTO;
import org.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${service.user.url}", path = "/api/users", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {
    @PostMapping
    ApiResponse<UserDTO> createUser(@RequestBody UserCreationDTO request);

    @GetMapping("/ids")
    ApiResponse<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<String> ids);

    @GetMapping("/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") String id);

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") String id);
}
