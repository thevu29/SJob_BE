package org.example.common.dto.S3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileDTO {
    private String id;
    private byte[] fileContent;
    private String fileName;
    private String contentType;
}
