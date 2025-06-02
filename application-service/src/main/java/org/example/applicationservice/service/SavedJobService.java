package org.example.applicationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.applicationservice.client.JobSeekerServiceClient;
import org.example.applicationservice.client.JobServiceClient;
import org.example.applicationservice.dto.CheckJobSeekerSaveJobDTO;
import org.example.applicationservice.dto.SavedJobCreationDTO;
import org.example.applicationservice.dto.SavedJobDTO;
import org.example.applicationservice.entity.SavedJob;
import org.example.applicationservice.mapper.SavedJobMapper;
import org.example.applicationservice.repository.SavedJobRepository;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobService {
    private final SavedJobMapper savedJobMapper;
    private final JobServiceClient jobServiceClient;
    private final SavedJobRepository savedJobRepository;
    private final JobSeekerServiceClient jobSeekerServiceClient;

    public boolean hasJobSeekerSavedJob(CheckJobSeekerSaveJobDTO request) {
        return savedJobRepository.findByJobIdAndJobSeekerId(request.getJobId(), request.getJobSeekerId())
                .isPresent();
    }

    public SavedJobDTO saveJob(SavedJobCreationDTO request) {
        CheckJobSeekerSaveJobDTO checkRequest = CheckJobSeekerSaveJobDTO.builder()
                .jobId(request.getJobId())
                .jobSeekerId(request.getJobSeekerId())
                .build();

        if (hasJobSeekerSavedJob(checkRequest)) {
            throw new IllegalArgumentException("Bạn đã lưu công việc này trước đó");
        }

        ApiResponse<JobDTO> jobResponse = jobServiceClient.getJobById(request.getJobId());
        ApiResponse<JobSeekerWithUserDTO> jobSeekerResponse = jobSeekerServiceClient.getJobSeekerById(request.getJobSeekerId());

        JobDTO job = jobResponse.getData();

        SavedJob savedJob = savedJobMapper.toEntity(request);
        savedJob.setJobId(job.getId());
        savedJob.setJobSeekerId(jobSeekerResponse.getData().getId());

        SavedJob createdSavedJob = savedJobRepository.save(savedJob);

        SavedJobDTO savedJobDto = savedJobMapper.toDTO(createdSavedJob);
        savedJobDto.setJob(job);

        return savedJobDto;
    }

    public void unSaveJob(String id) {
        jobServiceClient.getJobById(id);
        savedJobRepository.deleteById(id);
    }

    public Page<SavedJobDTO> getPaginatedSavedJobs(
            String jobSeekerId,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<SavedJob> savedJobs = savedJobRepository.findAll(pageable);

        if (jobSeekerId != null && !jobSeekerId.isBlank()) {
            savedJobs = savedJobRepository.findAllByJobSeekerId(jobSeekerId, pageable);
        }

        List<String> jobIds = savedJobs.stream().map(SavedJob::getJobId).toList();
        List<JobDTO> jobs = jobServiceClient.getJobByIds(jobIds).getData();

        return savedJobs.map(savedJob -> {
            SavedJobDTO savedJobDto = savedJobMapper.toDTO(savedJob);
            savedJobDto.setJob(jobs.stream().filter(job ->
                    job.getId().equals(savedJob.getJobId())).findFirst().orElse(null)
            );

            return savedJobDto;
        });
    }
}
