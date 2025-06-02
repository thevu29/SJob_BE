package org.example.applicationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.applicationservice.client.JobSeekerServiceClient;
import org.example.applicationservice.client.JobServiceClient;
import org.example.applicationservice.dto.ViewedJobCreationDTO;
import org.example.applicationservice.dto.ViewedJobDTO;
import org.example.applicationservice.entity.ViewedJob;
import org.example.applicationservice.mapper.ViewedJobMapper;
import org.example.applicationservice.repository.ViewedJobRepository;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViewedJobService {
    private final ViewedJobMapper viewedJobMapper;
    private final JobServiceClient jobServiceClient;
    private final ViewedJobRepository viewedJobRepository;
    private final JobSeekerServiceClient jobSeekerServiceClient;

    public ViewedJobDTO viewJob(ViewedJobCreationDTO request) {
        Optional<ViewedJob> existingViewedJob = viewedJobRepository.findByJobIdAndJobSeekerId(
                request.getJobId(), request.getJobSeekerId());

        if (existingViewedJob.isPresent()) {
            ViewedJob viewedJob = existingViewedJob.get();
            ViewedJobDTO viewedJobDto = viewedJobMapper.toDTO(viewedJob);
            viewedJobDto.setJob(jobServiceClient.getJobById(viewedJob.getJobId()).getData());

            return viewedJobDto;
        }

        ApiResponse<JobDTO> jobResponse = jobServiceClient.getJobById(request.getJobId());
        ApiResponse<JobSeekerWithUserDTO> jobSeekerResponse = jobSeekerServiceClient.getJobSeekerById(request.getJobSeekerId());

        JobDTO job = jobResponse.getData();

        ViewedJob viewedJob = viewedJobMapper.toEntity(request);
        viewedJob.setJobId(job.getId());
        viewedJob.setJobSeekerId(jobSeekerResponse.getData().getId());

        ViewedJob createdViewedJob = viewedJobRepository.save(viewedJob);

        ViewedJobDTO viewedJobDto = viewedJobMapper.toDTO(createdViewedJob);
        viewedJobDto.setJob(job);

        return viewedJobDto;
    }

    public Page<ViewedJobDTO> getPaginatedViewedJobs(
            String jobSeekerId,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ViewedJob> viewedJobs = viewedJobRepository.findAll(pageable);

        if (jobSeekerId != null && !jobSeekerId.isBlank()) {
            viewedJobs = viewedJobRepository.findAllByJobSeekerId(jobSeekerId, pageable);
        }

        List<String> jobIds = viewedJobs.stream().map(ViewedJob::getJobId).toList();
        List<JobDTO> jobs = jobServiceClient.getJobByIds(jobIds).getData();

        return viewedJobs.map(viewedJob -> {
            ViewedJobDTO viewedJobDto = viewedJobMapper.toDTO(viewedJob);
            viewedJobDto.setJob(jobs.stream().filter(job ->
                    job.getId().equals(viewedJob.getJobId())).findFirst().orElse(null)
            );

            return viewedJobDto;
        });
    }
}
