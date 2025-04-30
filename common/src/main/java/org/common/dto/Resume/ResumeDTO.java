package org.common.dto.Resume;

import lombok.Data;

@Data
public class ResumeDTO {
    private String id;
    private String name;
    private String url;
    private String uploadedAt;
    private boolean main;
    private String jobSeekerId;
}
