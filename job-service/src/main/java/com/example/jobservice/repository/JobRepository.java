package com.example.jobservice.repository;

import com.example.jobservice.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
}
