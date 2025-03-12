package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, String> {
}
