package com.example.jobservice.dto.Job.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {
    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotBlank(message = "Lương không được để trống")
    private String salary;

    @NotBlank(message = "Yêu cầu không được để trống")
    private String requirement;

    @NotBlank(message = "Quyền lợi không được để trống")
    private String benefit;

    @NotNull(message = "Hạn nộp hồ sơ không được để trống")
    @Future(message = "Hạn nộp hồ sơ phải là một ngày trong tương lai")
    private LocalDate deadline;

    @NotNull(message = "Số lượng tuyển dụng không được để trống")
    @Min(value = 1, message = "Số lượng tuyển dụng phải lớn hơn 0")
    private Integer slots;

    @NotBlank(message = "Kiểu công việc không được để trống")
    private String type;

    @NotBlank(message = "Trình độ học vấn không được để trống")
    private String education;

    @NotBlank(message = "Kinh nghiệm không được để trống")
    private String experience;

    @NotNull(message = "Ngành nghề/Lĩnh vực không được để trống")
    private String[] fieldDetails;
}
