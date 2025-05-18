package org.example.applicationservice.repository;

import org.example.applicationservice.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedJobRepository extends MongoRepository<SavedJob, String> {
    Page<SavedJob> findAllByJobSeekerId(String jobSeekerId, Pageable pageable);
}
