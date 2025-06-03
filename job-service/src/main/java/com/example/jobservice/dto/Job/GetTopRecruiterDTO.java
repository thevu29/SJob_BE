package com.example.jobservice.dto.Job;

import lombok.*;
import org.example.common.dto.Recruiter.RecruiterDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTopRecruiterDTO extends RecruiterDTO {
    private Long jobs;
}
