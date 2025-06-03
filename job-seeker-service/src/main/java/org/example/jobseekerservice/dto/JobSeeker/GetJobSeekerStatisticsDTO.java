package org.example.jobseekerservice.dto.JobSeeker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetJobSeekerStatisticsDTO {
    private int month;
    private int jobSeekers;
    private double percentageChange;
}
