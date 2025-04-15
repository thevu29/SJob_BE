package org.common.dto.JobSeeker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerWithUserDTO {
    private String id;
    private String userId;
    private String name;
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
