package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.common.exception.ResourceNotFoundException;
import org.example.jobseekerservice.dto.Resume.ResumeCreationDTO;
import org.example.jobseekerservice.dto.Resume.ResumeDTO;
import org.example.jobseekerservice.dto.Resume.ResumeUpdateDTO;
import org.example.jobseekerservice.entity.Resume;
import org.example.jobseekerservice.mapper.ResumeMapper;
import org.example.jobseekerservice.repository.ResumeRepository;
import org.example.jobseekerservice.utils.helpers.FileHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final FileHelper fileHelper;
    private final JobSeekerService jobSeekerService;

    private int countMainResumeByJobSeekerId(String jobSeekerId) {
        return resumeRepository.countByJobSeekerIdAndMainTrue(jobSeekerId);
    }

    private void deleteMainResumeByJobSeekerId(String jobSeekerId) {
        resumeRepository.findByJobSeekerIdAndMainTrue(jobSeekerId)
                .ifPresent(resume -> {
                    resume.setMain(false);
                    resumeRepository.save(resume);
                });
    }

    public List<ResumeDTO> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();

        return resumes.stream()
                .map(resumeMapper::toDto)
                .toList();
    }

    public List<ResumeDTO> getJobSeekerResumes(String jobSeekerId) {
        jobSeekerService.getJobSeekerById(jobSeekerId);

        List<Resume> resumes = resumeRepository.findByJobSeekerId(jobSeekerId);

        return resumes.stream()
                .map(resumeMapper::toDto)
                .toList();
    }

    public ResumeDTO getResumeById(String id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        return resumeMapper.toDto(resume);
    }

    public ResumeDTO createResume(ResumeCreationDTO request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Resume resume = resumeMapper.toEntity(request);

        if (request.isMain()) {
            deleteMainResumeByJobSeekerId(request.getJobSeekerId());
        } else {
            if (countMainResumeByJobSeekerId(request.getJobSeekerId()) == 0) {
                resume.setMain(true);
            }
        }

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                String fileUrl = fileHelper.uploadFile(request.getFile());
                resume.setUrl(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        Resume createdResume = resumeRepository.save(resume);

        return resumeMapper.toDto(createdResume);
    }

    public ResumeDTO updateResume(String id, ResumeUpdateDTO request) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (request.isMain()) {
            deleteMainResumeByJobSeekerId(resume.getJobSeeker().getId());
        }

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                fileHelper.deleteFile(resume.getUrl());

                String fileUrl = fileHelper.uploadFile(request.getFile());
                resume.setUrl(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        resumeMapper.toEntity(request, resume);
        Resume updatedResume = resumeRepository.save(resume);

        return resumeMapper.toDto(updatedResume);
    }

    public void deleteResume(String id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (resume.isMain()) {
            throw new RuntimeException("Main resume cannot be deleted");
        }

        fileHelper.deleteFile(resume.getUrl());

        resumeRepository.delete(resume);
    }
}
