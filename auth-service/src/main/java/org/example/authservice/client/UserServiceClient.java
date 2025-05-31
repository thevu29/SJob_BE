package org.example.authservice.client;

import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserUpdateOtpDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.authservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${service.user.url}", path = "/api/users", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {
    @GetMapping("/email/{email}")
    ApiResponse<UserDTO> getUserByEmail(@PathVariable String email);

    @PutMapping("/update-otp")
    ApiResponse<UserDTO> updateUserOTP(@RequestBody UserUpdateOtpDTO request);
}
