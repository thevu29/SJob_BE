package com.example.jobservice.repository;

import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.entity.Job;
import org.example.common.dto.Job.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    @Query("SELECT j FROM Job j WHERE j.deadline = :thresholdDate AND j.status = :status")
    List<Job> findByDeadlineAndStatus(LocalDate thresholdDate, JobStatus status);

    @Query(
            value = """
                    SELECT DISTINCT j.*
                    FROM job_service.jobs j
                    LEFT JOIN job_service.job_field jf ON j.id = jf.job_id
                    LEFT JOIN job_service.field_details fd ON jf.field_detail_id = fd.id
                    WHERE (:query IS NULL OR LOWER(j.name) LIKE LOWER(CONCAT('%', :query, '%')))
                      AND (:recruiterIds IS NULL OR j.recruiter_id IN (:recruiterIds))
                      AND (:type IS NULL OR j.type = :type)
                      AND (:status IS NULL OR j.status = :status)
                      AND (:recruiterId IS NULL OR j.recruiter_id = :recruiterId)
                      AND (:fieldDetailIds IS NULL OR fd.id IN (:fieldDetailIds))
                    ORDER BY j.date DESC
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT j.id)
                    FROM job_service.jobs j
                    LEFT JOIN job_service.job_field jf ON j.id = jf.job_id
                    LEFT JOIN job_service.field_details fd ON jf.field_detail_id = fd.id
                    WHERE (:query IS NULL OR LOWER(j.name) LIKE LOWER(CONCAT('%', :query, '%')))
                      AND (:recruiterIds IS NULL OR j.recruiter_id IN (:recruiterIds))
                      AND (:type IS NULL OR j.type = :type)
                      AND (:status IS NULL OR j.status = :status)
                      AND (:recruiterId IS NULL OR j.recruiter_id = :recruiterId)
                      AND (:fieldDetailIds IS NULL OR fd.id IN (:fieldDetailIds))
                    """,
            nativeQuery = true
    )
    Page<Job> findPaginatedJobs(
            @Param("query") String query,
            @Param("recruiterIds") List<String> recruiterIds,
            @Param("type") String type,
            @Param("status") String status,
            @Param("recruiterId") String recruiterId,
            @Param("fieldDetailIds") List<String> fieldDetailIds,
            Pageable pageable
    );

    @Query(value = """
            SELECT fd.* FROM job_service.field_details fd
            JOIN job_service.job_field jf ON fd.id = jf.field_detail_id
            WHERE jf.job_id = :jobId
            """, nativeQuery = true)
    List<FieldDetail> findFieldDetailsByJobId(@Param("jobId") String jobId);
}
