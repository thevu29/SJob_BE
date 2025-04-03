package org.example.jobseekerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean main = false;

    @ManyToOne
    @JoinColumn(nullable = false)
    private JobSeeker jobSeeker;
}
