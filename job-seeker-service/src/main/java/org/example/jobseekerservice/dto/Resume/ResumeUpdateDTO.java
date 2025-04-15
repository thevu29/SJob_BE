package org.example.jobseekerservice.dto.Resume;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResumeUpdateDTO {
    private String name;
    private MultipartFile file;
    private boolean main;
}
