package org.example.common.dto.JobSeeker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerWithUserDTO implements Serializable {
    private String id;
    private String userId;
    private String name;
    private String field;
    private String phone;
    private String image;
    private boolean gender;
    private String about;
    private String address;
    private boolean seeking;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
