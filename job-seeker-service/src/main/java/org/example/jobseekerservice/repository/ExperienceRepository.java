package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, String> {
    List<Experience> findByJobSeekerId(String jobSeekerId);
}
