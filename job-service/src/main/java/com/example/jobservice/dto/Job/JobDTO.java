package com.example.jobservice.dto.Job;

import com.example.jobservice.dto.FieldDetail.FieldDetailDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class JobDTO {
    private String id;
    private String recruiterId;
    private String name;
    private String description;
    private Double salary;
    private String requirement;
    private String benefit;
    private LocalDate deadline;
    private Integer slots;
    private String type;
    private LocalDate date;
    private String education;
    private String experience;
    private boolean closeWhenFull;
}
