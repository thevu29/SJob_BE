package com.example.jobservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "job_field")
public class JobField {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne()
    @JoinColumn(name = "job_id", nullable = false)
    @ToString.Exclude
    private Job job;

    @ManyToOne()
    @JoinColumn(name = "field_detail_id", nullable = false)
    @ToString.Exclude
    private FieldDetail fieldDetail;
}
