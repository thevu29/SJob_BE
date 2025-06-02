package org.example.applicationservice.repository;

import org.example.applicationservice.entity.ViewedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewedJobRepository extends MongoRepository<ViewedJob, String> {
    Page<ViewedJob> findAllByJobSeekerId(String jobSeekerId, Pageable pageable);

    Optional<ViewedJob> findByJobIdAndJobSeekerId(String jobId, String jobSeekerId);
}
