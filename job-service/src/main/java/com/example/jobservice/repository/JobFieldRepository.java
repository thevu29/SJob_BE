package com.example.jobservice.repository;

import com.example.jobservice.entity.JobField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobFieldRepository extends JpaRepository<JobField, String> {
    List<JobField> findByJobId(String jobId);
}
