package org.example.common.dto.Job;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JobWithRecruiterDTO {
    private String id;
    private String recruiterId;
    private String recruiterName;
    private String recruiterImage;
    private String name;
    private String description;
    private String salary;
    private String requirement;
    private String benefit;
    private LocalDate deadline;
    private Integer slots;
    private String type;
    private LocalDate date;
    private String education;
    private String experience;
    private boolean closeWhenFull;
    private JobStatus status;
}
