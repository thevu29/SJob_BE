package org.example.common.dto.JobSeeker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerUpdateEvent {
    private String id;
    private String name;
}
