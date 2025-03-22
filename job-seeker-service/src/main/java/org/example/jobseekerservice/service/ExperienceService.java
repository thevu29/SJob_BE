package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.Experience.ExperienceDTO;
import org.example.jobseekerservice.dto.Experience.request.CreateExperienceRequest;
import org.example.jobseekerservice.dto.Experience.request.UpdateExperienceRequest;
import org.example.jobseekerservice.entity.Experience;
import org.example.jobseekerservice.exception.ResourceNotFoundException;
import org.example.jobseekerservice.mapper.ExperienceMapper;
import org.example.jobseekerservice.repository.ExperienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper experienceMapper;
    private final JobSeekerService jobSeekerService;

    public List<ExperienceDTO> getAllExperiences() {
        List<Experience> experiences = experienceRepository.findAll();

        return experiences.stream()
                .map(experienceMapper::toDto)
                .toList();
    }

    public List<ExperienceDTO> getJobSeekerExperiences(String jobSeekerId) {
        jobSeekerService.getJobSeekerById(jobSeekerId);

        List<Experience> experiences = experienceRepository.findByJobSeekerId(jobSeekerId);

        return experiences.stream()
                .map(experienceMapper::toDto)
                .toList();
    }

    public ExperienceDTO getExperienceById(String experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));

        return experienceMapper.toDto(experience);
    }

    public ExperienceDTO createExperience(CreateExperienceRequest request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Experience experience = experienceMapper.toEntity(request);
        Experience createdExperience = experienceRepository.save(experience);

        return experienceMapper.toDto(createdExperience);
    }

    public ExperienceDTO updateExperience(String experienceId, UpdateExperienceRequest request) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));

        experienceMapper.toEntity(request, experience);

        Experience updatedExperience = experienceRepository.save(experience);

        return experienceMapper.toDto(updatedExperience);
    }

    public void deleteExperience(String experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));

        experienceRepository.delete(experience);
    }
}
