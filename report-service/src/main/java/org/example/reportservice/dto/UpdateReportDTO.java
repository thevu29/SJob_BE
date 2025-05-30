package org.example.reportservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateReportDTO {
    public String status;
}
