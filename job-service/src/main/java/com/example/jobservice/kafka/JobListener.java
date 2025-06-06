package com.example.jobservice.kafka;

import com.example.jobservice.client.JobSeekerServiceClient;
import com.example.jobservice.service.GeminiService;
import com.example.jobservice.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.common.dto.Job.JobWithRecruiterDTO;
import org.example.common.dto.JobSeeker.JobSeekerUpdateEvent;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobListener {
    private final JobService jobService;
    private final GeminiService geminiService;
    private final JobSeekerServiceClient jobSeekerServiceClient;

    @KafkaListener(topics = "job-seeker-update-events", groupId = "job-service")
    public void handleJobSeekerUpdate(ConsumerRecord<String, JobSeekerUpdateEvent> record) {
        JobSeekerUpdateEvent event = record.value();
        log.info("Received job seeker update event: {}", event);

        try {
            ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.getJobSeekerById(event.getId());

            JobSeekerWithUserDTO jobSeeker = response.getData();
            List<JobWithRecruiterDTO> allJobs = jobService.getAllJobs();

            geminiService.suggestJobs(jobSeeker, allJobs);
            log.info("Updated suggestJobs cache for jobSeekerId: {}", event.getId());
        } catch (Exception e) {
            log.error("Error updating suggestJobs cache for jobSeekerId: {}", event.getId(), e);
        }
    }
}
