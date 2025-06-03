package org.example.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetApplicationStatisticsDTO {
    private int month;
    private int applications;
    private double percentageChange;
}
