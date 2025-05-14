package com.example.recruiterservice.kafka;

import com.example.recruiterservice.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Job.JobUpdateEvent;
import org.example.common.dto.JobSeeker.JobSeekerUpdateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvitationListener {
    private final InvitationRepository invitationRepository;

    @KafkaListener(topics = "job-update-events", groupId = "invitation-service")
    public void handleJobUpdate(JobUpdateEvent event) {
        log.info("Received job update event: {}", event);
        invitationRepository.updateJobNameByJobId(event.getId(), event.getName());
    }

    @KafkaListener(topics = "job-seeker-update-events", groupId = "invitation-service")
    public void handleJobSeekerUpdate(JobSeekerUpdateEvent event) {
        log.info("Received job seeker update event: {}", event);
        invitationRepository.updateJobSeekerNameByJobSeekerId(event.getId(), event.getName());
    }
}

