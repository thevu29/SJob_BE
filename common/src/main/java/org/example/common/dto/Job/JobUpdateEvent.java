package org.example.common.dto.Job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobUpdateEvent {
    private String id;
    private String name;
}
