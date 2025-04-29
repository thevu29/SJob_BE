package com.example.jobservice.repository;

import com.example.jobservice.entity.Job;
import com.example.jobservice.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    @Query("SELECT j FROM Job j WHERE j.deadline = :thresholdDate AND j.status = :status")
    List<Job> findByDeadlineAndStatus(LocalDate thresholdDate, JobStatus status);

    @Query(value = """
    SELECT j.* FROM job_service.jobs j
    WHERE (CAST(:status AS VARCHAR) IS NULL OR CAST(j.status AS VARCHAR) = CAST(:status AS VARCHAR))
    AND (
        -- match by recruiterId (if provided)
        (CAST(:recruiterId AS VARCHAR) IS NOT NULL AND j.recruiter_id = :recruiterId)

        -- OR match by search query in specified fields
        OR (
            (CAST(:query AS VARCHAR) IS NOT NULL AND CAST(:query AS VARCHAR) <> '')
            AND (
                LOWER(j.name) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                OR LOWER(j.description) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                OR LOWER(j.requirement) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                OR LOWER(j.benefit) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
            )
        )
    )
    """,
            countQuery = """
        SELECT COUNT(*) FROM job_service.jobs j
        WHERE (CAST(:status AS VARCHAR) IS NULL OR CAST(j.status AS VARCHAR) = CAST(:status AS VARCHAR))
        AND (
            (CAST(:recruiterId AS VARCHAR) IS NOT NULL AND j.recruiter_id = :recruiterId)
            OR (
                (CAST(:query AS VARCHAR) IS NOT NULL AND CAST(:query AS VARCHAR) <> '')
                AND (
                    LOWER(j.name) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                    OR LOWER(j.description) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                    OR LOWER(j.requirement) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                    OR LOWER(j.benefit) LIKE LOWER(CONCAT('%', CAST(:query AS VARCHAR), '%'))
                )
            )
        )
        """,
            nativeQuery = true
    )
    Page<Job> findBySearchCriteria(
            @Param("query") String query,
            @Param("status") JobStatus status,
            @Param("recruiterId") String recruiterId,
            Pageable pageable
    );
}
