package com.example.jobservice.dto.Job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobStatisticsDTO {
    private Integer month;
    private Long jobs;
    private double percentageChange;

    public JobStatisticsDTO(Integer month, Long jobs) {
        this.month = month;
        this.jobs = jobs;
        this.percentageChange = 0.0;
    }
}

