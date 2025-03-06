package org.example.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.ApiResponse;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.UserCreationRequest;
import org.example.userservice.dto.request.UserUpdateRequest;
import org.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers() {
        List<UserDTO> users = userService.getUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String id) {
        UserDTO user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreationRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "User created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateRequest request) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully", HttpStatus.OK));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<ApiResponse<UserDTO>> blockUser(@PathVariable String id) {
        UserDTO blockedUser = userService.blockUser(id);
        return ResponseEntity.ok(ApiResponse.success(blockedUser, "User blocked successfully", HttpStatus.OK));
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse<UserDTO>> unblockUser(@PathVariable String id) {
        UserDTO unblockedUser = userService.unblockUser(id);
        return ResponseEntity.ok(ApiResponse.success(unblockedUser, "User unblocked successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully", HttpStatus.OK));
    }
}
