package org.example.applicationservice.repository;

import org.example.applicationservice.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    Page<Application> findAllByJobSeekerId(String jobSeekerId, Pageable pageable);

    Optional<Application> findByJobIdAndJobSeekerId(String jobId, String jobSeekerId);
}
