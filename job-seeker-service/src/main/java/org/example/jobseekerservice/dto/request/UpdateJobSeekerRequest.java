package org.example.jobseekerservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateJobSeekerRequest {
    private String name;

    @Pattern(regexp = "^[+]?[0-9]{10}$", message = "Phone number is invalid")
    private String phone;

    private MultipartFile imageFile;

    private boolean gender;

    private String about;

    private String address;

    private boolean seeking;
}
