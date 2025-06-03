package com.example.recruiterservice.dto.Recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRecruiterStatisticsDTO {
    private int month;
    private int recruiters;
    private double percentageChange;
}
