package com.example.jobservice.dto.Job.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateJobRequest {

    private String name;

    private String description;

    private String salary;

    private String requirement;

    private String benefit;

    @Future(message = "Hạn nộp hồ sơ phải là một ngày trong tương lai")
    private LocalDate deadline;

    @Min(value = 1, message = "Số lượng tuyển dụng phải lớn hơn 0")
    private Integer slots;

    private String type;

    private String education;

    private String experience;

    private String[] fieldDetails;

}
