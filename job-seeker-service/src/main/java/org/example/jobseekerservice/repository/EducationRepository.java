package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, String> {
    List<Education> findByJobSeekerId(String jobSeekerId);
}
