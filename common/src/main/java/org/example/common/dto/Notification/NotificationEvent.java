package org.example.common.dto.Notification;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public class NotificationEvent {
//    // Job Application Events
//    public static NotificationRequestDTO newJobApplication(
//            String userId,
//            String jobTitle,
//            String applicantId,
//            String applicationId) {
//
//        return NotificationRequestDTO.builder()
//                .userId(userId)
//                .type(NotificationType.JOB_APPLICATION)
//                .metaData(Map.of(
//                        "jobTitle", jobTitle,
//                        "applicantId", applicantId,
//                        "applicationId", applicationId,
//                        "applicationTime", new Date().toString()
//                ))
//                .build();
//    }
//
    public static NotificationRequestDTO applicationStatusUpdate(
            String userId,
            String email,
            String jobTitle,
            String companyName,
            String newStatus) {

        return NotificationRequestDTO.builder()
                .userId(userId)
                .email(email)
                .type(NotificationType.APPLICATION_STATUS)
                .metaData(Map.of(
                        "jobTitle", jobTitle,
                        "companyName", companyName,
                        "newStatus", newStatus,
                        "updateTime", new Date().toString()
                ))
                .build();
    }

    // System Announcement Events
    public static NotificationRequestDTO systemAnnouncement(
            String userId,
            String email,
            String title,
            String message
    ) {
        return NotificationRequestDTO.builder()
                .userId(userId)
                .email(email)
                .type(NotificationType.SYSTEM_ANNOUNCEMENT)
                .metaData(Map.of(
                        "title", title,
                        "message", message,
                        "announcementDate", new Date().toString()
                ))
                .build();
    }

    // Job Invitation Events
    public static NotificationRequestDTO jobInvitation(
            String userId,
            String email,
            String invitationId,
            String jobName,
            String recruiterId,
            String recruiterName,
            String message
    ) {
        return NotificationRequestDTO.builder()
                .userId(userId)
                .email(email)
                .type(NotificationType.JOB_INVITATION)
                .metaData(Map.of(
                        "invitationId", invitationId,
                        "jobName", jobName,
                        "recruiterId", recruiterId,
                        "recruiterName", recruiterName,
                        "message", message,
                        "invitationDate", new Date().toString()
                ))
                .build();
    }

    // Job Expiry Events
    public static NotificationRequestDTO jobExpiry(
            String userId,
            String email,
            String jobId,
            String jobName,
            LocalDate jobDeadline
    ) {
        return NotificationRequestDTO.builder()
                .userId(userId)
                .email(email)
                .type(NotificationType.JOB_EXPIRY)
                .metaData(Map.of(
                        "jobId", jobId,
                        "jobName", jobName,
                        "jobDeadline", jobDeadline.toString()
                ))
                .build();
    }

    public static NotificationRequestDTO jobApplication(
            String userId,
            String email,
            String applicantName,
            String jobTitle,
            String fileUrl,
            String message
    ) {
        return NotificationRequestDTO.builder()
                .userId(userId)
                .email(email)
                .type(NotificationType.JOB_APPLICATION)
                .metaData(Map.of(
                        "jobTitle", jobTitle,
                        "applicantName", applicantName,
                        "applicationTime", new Date().toString(),
                        "fileUrl", fileUrl,
                        "message", message
                ))
                .build();
    }
}
