package org.example.applicationservice.repository;

import org.example.applicationservice.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    @Aggregation(pipeline = {
            "{ $project: { month: { $month: '$date' }, year: { $year: '$date' } } }",
            "{ $match: { year: ?0, month: ?1 } }",
            "{ $count: 'count' }"
    })
    Integer countApplicationsInMonth(int year, int month);

    Page<Application> findAllByJobSeekerId(String jobSeekerId, Pageable pageable);

    Page<Application> findAllByJobId(String jobId, Pageable pageable);

    Optional<Application> findByJobIdAndJobSeekerId(String jobId, String jobSeekerId);
}
