package com.example.jobservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "field_details")
public class FieldDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "fieldDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<JobField> jobFields = new HashSet<>();

}
