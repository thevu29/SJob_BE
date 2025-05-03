package com.example.recruiterservice.entity.Invitation;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {
    @Id
    private String id;

    @Field("job_id")
    private String jobId;

    @Field("recruiter_id")
    private String recruiterId;

    @Field("job_seeker_id")
    private String jobSeekerId;

    private String message;

    private InvitationStatus status;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
