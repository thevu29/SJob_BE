package org.example.jobseekerservice.dto.Certifitcaion;

import lombok.Data;

@Data
public class CertificationDTO {
    public String id;
    public String jobSeekerId;
    public String name;
    public String issueDate;
    public String expireDate;
    public String imageOrFile;
}
