package org.example.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.common.dto.User.*;
import org.common.dto.response.ApiResponse;
import org.example.userservice.dto.UserUpdatePasswordDTO;
import org.example.userservice.dto.UserVerifyOtpDTO;
import org.example.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully", HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> findPagedUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "role", defaultValue = "ADMIN") String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<UserDTO> pages = userService.findPagedUsers(query, active, role, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Users fetched successfully"));
    }

    @GetMapping("ids")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByIds(@RequestParam("ids") List<String> ids) {
        List<UserDTO> users = userService.getUsersByIds(ids != null ? ids : List.of());
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreationDTO request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "User created successfully", HttpStatus.CREATED));
    }

    @PutMapping("/update-otp")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserOTP(@Valid @RequestBody UserUpdateOtpDTO request) {
        UserDTO updatedUser = userService.updateUserOtp(request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully", HttpStatus.OK));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<UserDTO>> verifyUserOtp(@Valid @RequestBody UserVerifyOtpDTO request) {
        UserDTO verifiedUser = userService.verifyUserOtp(request);
        return ResponseEntity.ok(ApiResponse.success(verifiedUser, "User verified successfully", HttpStatus.OK));
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserPassword(@Valid @RequestBody UserUpdatePasswordDTO request) {
        UserDTO updatedUser = userService.updateUserPassword(request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully", HttpStatus.OK));
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> blockUser(@PathVariable String id) {
        UserDTO blockedUser = userService.updateUserStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success(blockedUser, "User blocked successfully", HttpStatus.OK));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> activateUser(@PathVariable String id) {
        UserDTO activatedUser = userService.updateUserStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success(activatedUser, "User activated successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully", HttpStatus.OK));
    }
}
