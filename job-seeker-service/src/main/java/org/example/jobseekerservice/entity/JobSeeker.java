package org.example.jobseekerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "job_seekers",
        indexes = {
        @Index(name = "idx_jobseeker_userid", columnList = "user_id"),
        @Index(name = "idx_jobseeker_name", columnList = "name"),
        @Index(name = "idx_jobseeker_phone", columnList = "phone"),
        @Index(name = "idx_jobseeker_seeking", columnList = "seeking"),
        @Index(name = "idx_jobseeker_address", columnList = "address"),
        @Index(name = "idx_jobseeker_search", columnList = "name, phone, address")
    }
)
public class JobSeeker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    private String image;

    @Column(nullable = false)
    private boolean gender;

    private String about;

    @Column(nullable = false)
    private String address;

    @Builder.Default
    private boolean seeking = false;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Resume> resumes;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Certification> certifications;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Skill> skills;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Education> educations;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Experience> experiences;
}
