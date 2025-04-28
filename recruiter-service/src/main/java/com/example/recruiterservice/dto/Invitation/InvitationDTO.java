package com.example.recruiterservice.dto.Invitation;

import com.example.recruiterservice.entity.Invitation.InvitationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvitationDTO {
    private String id;
    private String jobId;
    private String recruiterId;
    private String jobSeekerId;
    private String message;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
