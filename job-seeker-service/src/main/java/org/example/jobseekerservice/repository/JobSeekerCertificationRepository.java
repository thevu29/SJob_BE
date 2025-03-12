package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.JobSeekerCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerCertificationRepository extends JpaRepository<JobSeekerCertification, String> {
}
