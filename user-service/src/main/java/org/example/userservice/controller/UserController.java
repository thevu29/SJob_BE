package org.example.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.dto.response.ApiResponse;
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

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers() {
        List<UserDTO> users = userService.getUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully", HttpStatus.OK));
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
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "User created successfully", HttpStatus.CREATED));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<ApiResponse<UserDTO>> blockUser(@PathVariable String id) {
        UserDTO blockedUser = userService.updateUserStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success(blockedUser, "User blocked successfully", HttpStatus.OK));
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse<UserDTO>> unblockUser(@PathVariable String id) {
        UserDTO unblockedUser = userService.updateUserStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success(unblockedUser, "User unblocked successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> softDeleteUser(@PathVariable String id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ApiResponse<UserDTO>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully", HttpStatus.OK));
    }
}
