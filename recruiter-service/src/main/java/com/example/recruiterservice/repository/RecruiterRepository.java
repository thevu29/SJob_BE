package com.example.recruiterservice.repository;

import com.example.recruiterservice.entity.Recruiter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
}
