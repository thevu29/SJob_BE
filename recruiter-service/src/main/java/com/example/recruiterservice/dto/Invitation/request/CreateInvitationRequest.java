package com.example.recruiterservice.dto.Invitation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateInvitationRequest {

    @NotBlank(message = "Không được để trống ID công việc")
    private String jobId;

    @NotBlank(message = "Không được để trống tên công việc")
    private String jobName;

    @NotBlank(message = "Không được để trống ID nhà tuyển dụng")
    private String recruiterId;

    @NotBlank(message = "Không được để trống ID ứng viên")
    private String jobSeekerId;

    @NotBlank(message = "Không được để trống tên ứng viên")
    private String jobSeekerName;

    @NotBlank(message = "Không được để trống tin nhắn")
    private String message;
}
