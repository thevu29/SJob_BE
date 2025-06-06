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
//    public static NotificationRequestDTO applicationStatusUpdate(
//            String jobSeekerId,
//            String jobId,
//            String jobTitle,
//            String companyName,
//            String applicationId,
//            String newStatus) {
//
//        return NotificationRequestDTO.builder()
//                .recipientId(jobSeekerId)
//                .recipientType("JOB_SEEKER")
//                .type(NotificationType.APPLICATION_STATUS)
//                .data(Map.of(
//                        "jobId", jobId,
//                        "jobTitle", jobTitle,
//                        "companyName", companyName,
//                        "applicationId", applicationId,
//                        "newStatus", newStatus,
//                        "updateTime", new Date().toString()
//                ))
//                .build();
//    }
//
//    // Job Recommendation Events
//    public static NotificationRequestDTO jobRecommendation(
//            String jobSeekerId,
//            List<Map<String, Object>> recommendedJobs) {
//
//        return NotificationRequestDTO.builder()
//                .recipientId(jobSeekerId)
//                .recipientType("JOB_SEEKER")
//                .type(NotificationType.JOB_RECOMMENDATION)
//                .data(Map.of(
//                        "jobs", recommendedJobs,
//                        "recommendationDate", new Date().toString()
//                ))
//                .build();
//    }

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
            String applicationId
    ) {
        return NotificationRequestDTO.builder()
                .userId(userId)
                .email(email)
                .type(NotificationType.JOB_APPLICATION)
                .metaData(Map.of(
                        "jobTitle", jobTitle,
                        "applicantName", applicantName,
                        "applicationId", applicationId,
                        "applicationTime", new Date().toString()
                ))
                .build();
    }
}
