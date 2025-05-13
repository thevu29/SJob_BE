package com.example.jobservice.repository;

import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.entity.Job;
import org.example.common.dto.Job.JobStatus;
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
            WHERE (:status IS NULL OR j.status::text ILIKE CONCAT('%', :status, '%'))
              AND (:type IS NULL OR j.type::text ILIKE CONCAT('%', :type, '%'))
              AND (:recruiterId IS NULL OR j.recruiter_id = :recruiterId)
              AND (
                  :query IS NULL OR
                  LOWER(j.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                  LOWER(j.experience) LIKE LOWER(CONCAT('%', :query, '%')) OR
                  LOWER(j.education) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """, nativeQuery = true)
    Page<Job> findBySearchCriteria(
            @Param("query") String query,
            @Param("status") String status,
            @Param("type") String type,
            @Param("recruiterId") String recruiterId,
            Pageable pageable
    );

    @Query(value = """
    SELECT fd.* FROM job_service.field_details fd
    JOIN job_service.job_field jf ON fd.id = jf.field_detail_id
    WHERE jf.job_id = :jobId
    """, nativeQuery = true)
    List<FieldDetail> findFieldDetailsByJobId(@Param("jobId") String jobId);

    List<Job> findByRecruiterId(String recruiterId);
}
