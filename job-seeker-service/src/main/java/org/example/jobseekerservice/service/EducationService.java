package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.Education.EducationDTO;
import org.example.jobseekerservice.dto.Education.request.CreateEducationRequest;
import org.example.jobseekerservice.dto.Education.request.UpdateEducationRequest;
import org.example.jobseekerservice.entity.Education;
import org.example.jobseekerservice.exception.ResourceNotFoundException;
import org.example.jobseekerservice.mapper.EducationMapper;
import org.example.jobseekerservice.repository.EducationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {
    private final JobSeekerService jobSeekerService;
    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    public List<EducationDTO> getEducations() {
        List<Education> educations = educationRepository.findAll();
        return educations.stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    public EducationDTO getEducationById(String id) {
        Education education = educationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        return educationMapper.toDto(education);
    }

    public EducationDTO createEducation(CreateEducationRequest request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Education education = educationMapper.toEntity(request);

        if (education.getStartDate().isAfter(education.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Education createdEducation = educationRepository.save(education);

        return educationMapper.toDto(createdEducation);
    }

    public EducationDTO updateEducation(String id, UpdateEducationRequest request) {
        Education education = educationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        educationMapper.toEntity(request, education);
        Education updatedEducation = educationRepository.save(education);

        return educationMapper.toDto(updatedEducation);
    }

    public void deleteEducation(String id) {
        Education education = educationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        educationRepository.delete(education);
    }
}
