package org.example.jobseekerservice.service;

import lombok.RequiredArgsConstructor;
import org.common.exception.ResourceNotFoundException;
import org.example.jobseekerservice.dto.Certifitcaion.CertificationCreationDTO;
import org.example.jobseekerservice.dto.Certifitcaion.CertificationDTO;
import org.example.jobseekerservice.dto.Certifitcaion.CertificationUpdateDTO;
import org.example.jobseekerservice.entity.Certification;
import org.example.jobseekerservice.mapper.CertificationMapper;
import org.example.jobseekerservice.repository.CertificationRepository;
import org.example.jobseekerservice.utils.helpers.FileHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final JobSeekerService jobSeekerService;
    private final CertificationRepository certificationRepository;
    private final CertificationMapper certificationMapper;
    private final FileHelper fileHelper;

    public List<CertificationDTO> getCertifications() {
        List<Certification> certifications = certificationRepository.findAll();

        return certifications.stream()
                .map(certificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CertificationDTO> getJobSeekerCertifications(String jobSeekerId) {
        jobSeekerService.getJobSeekerById(jobSeekerId);

        List<Certification> certifications = certificationRepository.findByJobSeekerId(jobSeekerId);

        return certifications.stream()
                .map(certificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public CertificationDTO getCertificationById(String certificationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));

        return certificationMapper.toDto(certification);
    }

    public CertificationDTO createCertification(CertificationCreationDTO request) {
        jobSeekerService.getJobSeekerById(request.getJobSeekerId());

        Certification certification = certificationMapper.toEntity(request);

        if (certification.getIssueDate().isAfter(certification.getExpireDate())) {
            throw new RuntimeException("Issue date must be before expire date");
        }

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                String fileUrl = fileHelper.uploadFile(request.getFile());
                certification.setImageOrFile(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        Certification createdCertification = certificationRepository.save(certification);

        return certificationMapper.toDto(createdCertification);
    }

    public CertificationDTO updateCertification(String certificationId, CertificationUpdateDTO request) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));

        if (certification.getIssueDate().isAfter(certification.getExpireDate())) {
            throw new RuntimeException("Issue date must be before expire date");
        }

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                if (certification.getImageOrFile() != null && !certification.getImageOrFile().isEmpty()) {
                    fileHelper.deleteFile(certification.getImageOrFile());
                }

                String fileUrl = fileHelper.uploadFile(request.getFile());
                certification.setImageOrFile(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        certificationMapper.toEntity(request, certification);
        Certification updatedCertification = certificationRepository.save(certification);

        return certificationMapper.toDto(updatedCertification);
    }

    public void deleteCertification(String certificationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));

        if (certification.getImageOrFile() != null && !certification.getImageOrFile().isEmpty()) {
            fileHelper.deleteFile(certification.getImageOrFile());
        }

        certificationRepository.delete(certification);
    }
}
