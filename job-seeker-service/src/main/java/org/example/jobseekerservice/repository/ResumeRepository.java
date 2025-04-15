package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {
    Optional<Resume> findByJobSeekerIdAndMainTrue(String jobSeekerId);

    int countByJobSeekerIdAndMainTrue(String jobSeekerId);

    List<Resume> findByJobSeekerId(String jobSeekerId);
}
