package com.example.recruiterservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String id;

    private String email;

    private String role;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}
