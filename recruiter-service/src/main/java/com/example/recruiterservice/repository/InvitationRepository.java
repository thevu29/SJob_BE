package com.example.recruiterservice.repository;

import com.example.recruiterservice.entity.Invitation.Invitation;

import com.example.recruiterservice.entity.Invitation.InvitationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
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
}
