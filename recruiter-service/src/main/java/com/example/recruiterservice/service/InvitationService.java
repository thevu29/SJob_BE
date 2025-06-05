package com.example.recruiterservice.service;

import com.example.recruiterservice.client.JobSeekerServiceClient;
import com.example.recruiterservice.client.JobServiceClient;
import com.example.recruiterservice.dto.Invitation.InvitationDTO;
import com.example.recruiterservice.dto.Invitation.request.CreateInvitationRequest;
import com.example.recruiterservice.entity.Invitation.Invitation;
import com.example.recruiterservice.entity.Invitation.InvitationStatus;
import com.example.recruiterservice.mapper.InvitationMapper;
import com.example.recruiterservice.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Job.JobDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.example.common.dto.Notification.NotificationEvent;
import org.example.common.dto.Notification.NotificationRequestDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.exception.IllegalStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final RecruiterService recruiterService;
    private final JobSeekerServiceClient jobSeekerServiceClient;
    private final JobServiceClient jobServiceClient;
    private final KafkaTemplate <String, NotificationRequestDTO> kafkaTemplate;

    public InvitationDTO createInvitation(CreateInvitationRequest request) {
        // Check for existing non-rejected invitation
        boolean invitationExists = invitationRepository.existsByJobIdAndJobSeekerIdAndRecruiterIdAndStatusIn(
                request.getJobId(),
                request.getJobSeekerId(),
                request.getRecruiterId(),
                List.of(InvitationStatus.PENDING, InvitationStatus.ACCEPTED)
        );

        if (invitationExists) {
            throw new IllegalStateException("Đã gửi lời mời ứng tuyển cho ứng viên này");
        }

        Invitation invitation = invitationMapper.toEntity(request);
        Invitation savedInvitation = invitationRepository.save(invitation);

        // Send notification
        JobSeekerWithUserDTO jobSeeker = jobSeekerServiceClient.getJobSeekerById(request.getJobSeekerId()).getData();
        String userId = jobSeeker.getUserId();
        String email = jobSeeker.getEmail();

        JobDTO jobDTO = jobServiceClient.getJob(request.getJobId()).getData();
        String jobName = jobDTO.getName();

        RecruiterWithUserDTO recruiter = recruiterService.getRecruiterById(request.getRecruiterId());
        String recruiterName = recruiter.getName();

        NotificationRequestDTO notificationRequestDTO = NotificationEvent.jobInvitation(userId, email, savedInvitation.getId(), jobName, request.getRecruiterId(), recruiterName, request.getMessage());
        kafkaTemplate.send("notification-requests", notificationRequestDTO);

        return invitationMapper.toDto(savedInvitation);
    }

    public Page<InvitationDTO> findInvitations(
            String query,
            InvitationStatus status,
            String recruiterId,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        String columnName = switch (sortBy) {
            case "jobName" -> "job_name";
            case "jobSeekerName" -> "job_seeker_name";
            case "status" -> "status";
            case "createdAt" -> "created_at";
            default -> "id";
        };

        Sort sort = Sort.by(direction, columnName);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        Page<Invitation> invitations = invitationRepository.findBySearchCriteria(
                query != null ? query : "",
                status != null ? status.name() : "",
                recruiterId != null ? recruiterId : "",
                pageRequest
        );

        if (invitations.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }

        List<InvitationDTO> content = invitations.getContent().stream()
                .map(invitationMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, invitations.getTotalElements());
    }

    public void deleteInvitation(String invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Lời mời không tồn tại"));

        invitationRepository.delete(invitation);
    }
}
