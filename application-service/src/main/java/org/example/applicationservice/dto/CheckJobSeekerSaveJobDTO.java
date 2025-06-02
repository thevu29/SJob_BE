package org.example.applicationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckJobSeekerSaveJobDTO {
    @NotBlank(message = "Mã công việc không được để trống")
    private String jobId;

    @NotBlank(message = "Mã người tìm việc không được để trống")
    private String jobSeekerId;
}
