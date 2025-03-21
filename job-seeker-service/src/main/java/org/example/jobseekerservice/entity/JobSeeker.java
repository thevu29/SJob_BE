package org.example.jobseekerservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "job_seekers")
public class JobSeeker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, name = "user_id")
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
