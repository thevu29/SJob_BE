package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.exception.ResourceNotFoundException;
import org.example.jobseekerservice.dto.Education.EducationCreationDTO;
import org.example.jobseekerservice.dto.Education.EducationDTO;
import org.example.jobseekerservice.dto.Education.EducationUpdateDTO;
import org.example.jobseekerservice.entity.Education;
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

    public List<EducationDTO> getJobSeekerEducations(String jobSeekerId) {
        JobSeekerWithUserDTO jobSeeker = jobSeekerService.getJobSeekerById(jobSeekerId);

        List<Education> educations = educationRepository.findByJobSeekerId(jobSeeker.getId());

        return educations.stream()
                .map(educationMapper::toDto)
                .collect(Collectors.toList());
    }

    public EducationDTO getEducationById(String id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        return educationMapper.toDto(education);
    }

    public EducationDTO createEducation(EducationCreationDTO request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Education education = educationMapper.toEntity(request);

        if (education.getEndDate() != null && education.getStartDate().isAfter(education.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Education createdEducation = educationRepository.save(education);

        return educationMapper.toDto(createdEducation);
    }

    public EducationDTO updateEducation(String id, EducationUpdateDTO request) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        if (education.getStartDate().isAfter(education.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        educationMapper.toEntity(request, education);

        if (request.getEndDate() == null) {
            education.setEndDate(null);
        }

        Education updatedEducation = educationRepository.save(education);

        return educationMapper.toDto(updatedEducation);
    }

    public void deleteEducation(String id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        educationRepository.delete(education);
    }
}
