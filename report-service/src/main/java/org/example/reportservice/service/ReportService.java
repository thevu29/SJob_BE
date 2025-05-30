package org.example.reportservice.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.reportservice.client.JobSeekerServiceClient;
import org.example.reportservice.client.RecruiterServiceClient;
import org.example.reportservice.dto.CreateReportDTO;
import org.example.reportservice.dto.ReportDTO;
import org.example.reportservice.dto.UpdateReportDTO;
import org.example.reportservice.entity.Report;
import org.example.reportservice.entity.ReportStatus;
import org.example.reportservice.exception.ResourceNotFoundException;
import org.example.reportservice.mapper.ReportMapper;
import org.example.reportservice.repository.ReportRepository;
import org.example.reportservice.util.FileHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final FileHelper fileHelper;
    private final ReportMapper reportMapper;
    private final ReportRepository reportRepository;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;

    public Page<ReportDTO> getPaginatedReports(
            String query,
            String status,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        List<String> jobSeekerIds = List.of();
        List<String> recruiterIds = List.of();
        List<JobSeekerWithUserDTO> jobSeekers = List.of();
        List<RecruiterWithUserDTO> recruiters = List.of();

        if (query != null && !query.isBlank()) {
            ApiResponse<List<JobSeekerWithUserDTO>> jobSeekerResponse = jobSeekerServiceClient.getJobSeekers(query, page, size);

            if (jobSeekerResponse.getData() != null) {
                jobSeekers = jobSeekerResponse.getData();

                jobSeekerIds = jobSeekers.stream()
                        .map(JobSeekerWithUserDTO::getId)
                        .toList();
            }

            ApiResponse<List<RecruiterWithUserDTO>> recruiterResponse = recruiterServiceClient.getRecruiters(query, page, size);
            if (recruiterResponse.getData() != null) {
                recruiters = recruiterResponse.getData();

                recruiterIds = recruiters.stream()
                        .map(RecruiterWithUserDTO::getId)
                        .toList();
            }
        }

        ReportStatus reportStatus = null;

        if (status != null && !status.isBlank()) {
            reportStatus = ReportStatus.valueOf(status.toUpperCase());
        }

        boolean hasJobSeekerIds = !jobSeekerIds.isEmpty();
        boolean hasRecruiterIds = !recruiterIds.isEmpty();
        boolean hasStatus = reportStatus != null;

        Page<Report> reports = reportRepository.getPaginatedReports(
                jobSeekerIds,
                recruiterIds,
                hasJobSeekerIds,
                hasRecruiterIds,
                reportStatus,
                hasStatus,
                pageable
        );

        if (reports.isEmpty()) {
            return Page.empty(pageable);
        }

        Map<String, JobSeekerWithUserDTO> jobSeekerMap = jobSeekerIds.isEmpty() ? Map.of() :
                jobSeekers.stream().collect(Collectors.toMap(
                        JobSeekerWithUserDTO::getId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<String, RecruiterWithUserDTO> recruiterMap = recruiterIds.isEmpty() ? Map.of() :
                recruiters.stream().collect(Collectors.toMap(
                        RecruiterWithUserDTO::getId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        return convertToDTOPage(reports, jobSeekerMap, recruiterMap);
    }

    public ReportDTO getReportById(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo không tồn tại"));

        String jobSeekerEmail = null;
        String recruiterEmail = null;

        if (report.getJobSeekerId() != null) {
            ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.getJobSeekerById(report.getJobSeekerId());

            if (response.getData() != null) {
                jobSeekerEmail = response.getData().getEmail();
            }
        }

        if (report.getRecruiterId() != null) {
            ApiResponse<RecruiterWithUserDTO> response = recruiterServiceClient.getRecruiterById(report.getRecruiterId());

            if (response.getData() != null) {
                recruiterEmail = response.getData().getEmail();
            }
        }

        return convertToDTO(report, jobSeekerEmail, recruiterEmail);
    }

    public ReportDTO createReport(CreateReportDTO request) {
        if (
                (request.getJobSeekerId() == null || request.getJobSeekerId().isBlank()) &&
                (request.getRecruiterId() == null || request.getRecruiterId().isBlank())
        ) {
            throw new IllegalArgumentException("Cần cung cấp ID của người tìm việc hoặc nhà tuyển dụng");
        }

        if (request.getEvidenceFile() == null || request.getEvidenceFile().isEmpty()) {
            throw new IllegalArgumentException("Cần cung cấp ảnh chứng cứ");
        }

        String jobSeekerEmail = null;
        String recruiterEmail = null;

        if (request.getJobSeekerId() != null && !request.getJobSeekerId().isBlank()) {
            ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.getJobSeekerById(request.getJobSeekerId());

            if (response.getData() != null) {
                jobSeekerEmail = response.getData().getEmail();
            }
        }

        if (request.getRecruiterId() != null && !request.getRecruiterId().isBlank()) {
            ApiResponse<RecruiterWithUserDTO> response = recruiterServiceClient.getRecruiterById(request.getRecruiterId());

            if (response.getData() != null) {
                recruiterEmail = response.getData().getEmail();
            }
        }

        Report report = reportMapper.toEntity(request);

        try {
            String evidenceUrl = fileHelper.uploadFile(request.getEvidenceFile());
            report.setEvidence(evidenceUrl);
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại", e);
        }

        Report savedReport = reportRepository.save(report);

        return convertToDTO(savedReport, jobSeekerEmail, recruiterEmail);
    }

    public ReportDTO updateReport(String reportId, UpdateReportDTO request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo không tồn tại"));

        reportMapper.toEntity(request, report);

        Report updatedReport = reportRepository.save(report);

        return convertToDTO(updatedReport, null, null);
    }

    public void deleteReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo không tồn tại"));

        reportRepository.delete(report);
    }

    private Page<ReportDTO> convertToDTOPage(
            Page<Report> reports,
            Map<String, JobSeekerWithUserDTO> jobSeekerMap,
            Map<String, RecruiterWithUserDTO> recruiterMap
    ) {
        return reports.map(report -> {
            String jobSeekerEmail = null;
            String recruiterEmail = null;

            if (report.getJobSeekerId() != null) {
                JobSeekerWithUserDTO jobSeeker = jobSeekerMap.get(report.getJobSeekerId());

                if (jobSeeker != null) {
                    jobSeekerEmail = jobSeeker.getEmail();
                }
            }

            if (report.getRecruiterId() != null) {
                RecruiterWithUserDTO recruiter = recruiterMap.get(report.getRecruiterId());

                if (recruiter != null) {
                    recruiterEmail = recruiter.getEmail();
                }
            }

            return convertToDTO(report, jobSeekerEmail, recruiterEmail);
        });
    }

    private ReportDTO convertToDTO(Report report, String jobSeekerEmail, String recruiterEmail) {
        ReportDTO reportDTO = reportMapper.toDTO(report);

        if (jobSeekerEmail == null && report.getJobSeekerId() != null && !report.getJobSeekerId().isBlank()) {
            ApiResponse<JobSeekerWithUserDTO> response = jobSeekerServiceClient.getJobSeekerById(report.getJobSeekerId());
            jobSeekerEmail = response.getData() != null ? response.getData().getEmail() : "";
        }

        if (recruiterEmail == null && report.getRecruiterId() != null && !report.getRecruiterId().isBlank()) {
            ApiResponse<RecruiterWithUserDTO> response = recruiterServiceClient.getRecruiterById(report.getRecruiterId());
            recruiterEmail = response.getData() != null ? response.getData().getEmail() : "";
        }

        reportDTO.setJobSeekerEmail(jobSeekerEmail != null ? jobSeekerEmail : "");
        reportDTO.setRecruiterEmail(recruiterEmail != null ? recruiterEmail : "");

        return reportDTO;
    }
}
