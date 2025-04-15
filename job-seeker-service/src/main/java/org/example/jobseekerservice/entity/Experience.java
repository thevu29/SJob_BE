package org.example.jobseekerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.jobseekerservice.enums.EmployeeType;
import org.example.jobseekerservice.enums.LocationType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "experiences")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String position;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType locationType;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeType employeeType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private JobSeeker jobSeeker;
}
