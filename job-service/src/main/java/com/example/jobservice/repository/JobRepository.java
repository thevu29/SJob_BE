package com.example.jobservice.repository;

import com.example.jobservice.dto.Job.GetJobStatisticsDTO;
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
import java.util.Map;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, QuerydslPredicateExecutor<Job> {
    @Query(value = """
            SELECT j.recruiter_id AS recruiterId, COUNT(*) AS jobs
            FROM job_service.jobs j
            GROUP BY j.recruiter_id
            ORDER BY jobs DESC
            LIMIT 5
            """,
            nativeQuery = true)
    List<Map<String, Object>> getTop5RecruitersWithMostJobs();

    @Query(value = """
            SELECT COUNT(*) AS count
            FROM job_service.jobs j
            WHERE EXTRACT(YEAR FROM j.date) = :year
            AND EXTRACT(MONTH FROM j.date) = :month
            """,
            nativeQuery = true)
    Integer countJobsInMonth(@Param("year") int year, @Param("month") int month);

    @Query(value = """
            SELECT EXTRACT(MONTH FROM j.date) AS month, COUNT(*) AS jobs
            FROM job_service.jobs j
            WHERE EXTRACT(YEAR FROM j.date) = :year
            GROUP BY EXTRACT(MONTH FROM j.date)
            ORDER BY month
            """,
            nativeQuery = true)
    List<GetJobStatisticsDTO> countJobsByMonthInYear(@Param("year") int year);

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
