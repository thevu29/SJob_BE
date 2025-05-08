package org.example.jobseekerservice.dto.JobSeeker;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class JobSeekerUpdateDTO {
    private String name;

    private String field;

    @Pattern(regexp = "^[+]?[0-9]{10}$", message = "Phone number is invalid")
    private String phone;

    @Schema(type = "string", format = "binary")
    private MultipartFile imageFile;

    private boolean gender;

    private String about;

    private String address;

    private boolean seeking;
}
