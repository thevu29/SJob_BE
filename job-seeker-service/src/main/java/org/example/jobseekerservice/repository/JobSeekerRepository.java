package org.example.jobseekerservice.repository;

import org.example.jobseekerservice.entity.JobSeeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, String> {
    @Query(value = """
            SELECT js.* FROM job_seeker_service.job_seekers js
            WHERE (:seeking IS NULL OR js.seeking = :seeking)
            AND (
                -- match by userIds (if provided)
                (:userIds IS NOT NULL AND js.user_id IN (:userIds))
            
                -- OR match by search query in specified fields
                OR (
                    (:query IS NOT NULL AND :query <> '')
                    AND (
                        LOWER(js.name) LIKE LOWER(CONCAT('%', :query, '%'))
                        OR LOWER(js.phone) LIKE LOWER(CONCAT('%', :query, '%'))
                        OR LOWER(js.address) LIKE LOWER(CONCAT('%', :query, '%'))
                        OR LOWER(js.field) LIKE LOWER(CONCAT('%', :query, '%'))
                    )
                )
            )
            """,
            countQuery = """
                    SELECT COUNT(*) FROM job_seekers js
                    WHERE (:seeking IS NULL OR js.seeking = :seeking)
                    AND (
                        -- match by userIds (if provided)
                        (:userIds IS NOT NULL AND js.user_id IN (:userIds))
                    
                        -- OR match by search query in specified fields
                        OR (
                            (:query IS NOT NULL AND :query <> '')
                            AND (
                                LOWER(js.name) LIKE LOWER(CONCAT('%', :query, '%'))
                                OR LOWER(js.phone) LIKE LOWER(CONCAT('%', :query, '%'))
                                OR LOWER(js.address) LIKE LOWER(CONCAT('%', :query, '%'))
                                OR LOWER(js.field) LIKE LOWER(CONCAT('%', :query, '%'))
                            )
                        )
                    )
                    """,
            nativeQuery = true
    )
    Page<JobSeeker> findBySearchCriteria(
            @Param("query") String query,
            @Param("userIds") List<String> userIds,
            @Param("seeking") Boolean seeking,
            Pageable pageable
    );
}