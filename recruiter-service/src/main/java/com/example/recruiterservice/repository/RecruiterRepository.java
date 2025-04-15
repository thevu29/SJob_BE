package com.example.recruiterservice.repository;

import com.example.recruiterservice.dto.RecruiterDTO;
import com.example.recruiterservice.entity.Recruiter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
    List<Recruiter> findByNameIn(Collection<String> recruiterNames);
}
