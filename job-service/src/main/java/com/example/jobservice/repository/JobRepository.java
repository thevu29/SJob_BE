package com.example.jobservice.repository;

import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.entity.Job;
import org.example.common.dto.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, QuerydslPredicateExecutor<Job> {
    @Query("SELECT j FROM Job j WHERE j.deadline = :thresholdDate AND j.status = :status")
    List<Job> findByDeadlineAndStatus(LocalDate thresholdDate, JobStatus status);

    @Query(
            value = """
                    SELECT fd.* FROM job_service.field_details fd
                    JOIN job_service.job_field jf ON fd.id = jf.field_detail_id
                    WHERE jf.job_id = :jobId
                    """,
            nativeQuery = true
    )
    List<FieldDetail> findFieldDetailsByJobId(@Param("jobId") String jobId);
}
