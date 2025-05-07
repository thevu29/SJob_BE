package com.example.recruiterservice.repository;

import com.example.recruiterservice.entity.Invitation.Invitation;

import com.example.recruiterservice.entity.Invitation.InvitationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends MongoRepository<Invitation, String> {
    boolean existsByJobIdAndJobSeekerIdAndRecruiterIdAndStatusIn(
            String jobId,
            String jobSeekerId,
            String recruiterId,
            List<InvitationStatus> statuses
    );


    @Query("{ 'jobId': ?0 }")
    @Update("{ '$set': { 'jobName': ?1 } }")
    void updateJobNameByJobId(String id, String name);

    @Query("{ 'jobSeekerId': ?0 }")
    @Update("{ '$set': { 'jobSeekerName': ?1 } }")
    void updateJobSeekerNameByJobSeekerId(String id, String name);
}
