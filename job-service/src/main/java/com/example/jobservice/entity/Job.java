package com.example.jobservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "recruiter_id")
    private String recruiterId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false)
    private String requirement;

    @Column(nullable = false)
    private String benefit;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private Integer slots;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private String experience;

    @Column(nullable = false, name = "close_when_full")
    private boolean closeWhenFull;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

}
