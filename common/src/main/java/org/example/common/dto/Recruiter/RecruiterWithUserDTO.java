package org.example.common.dto.Recruiter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterWithUserDTO {
    private String id;
    private String userId;
    private String fieldId;
    private String name;
    private String about;
    private String image;
    private String website;
    private String address;
    private Integer members;
    private Boolean status;
    private String email;
    private String fieldName;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
