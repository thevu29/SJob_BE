package org.example.jobseekerservice.dto.Resume.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateResumeRequest {
    private String name;
    private MultipartFile file;
    private boolean main;
}
