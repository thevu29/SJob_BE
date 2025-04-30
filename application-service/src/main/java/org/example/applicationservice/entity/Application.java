package org.example.applicationservice.entity;

import lombok.*;
import org.example.applicationservice.enums.ApplicationStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    private String id;

    @Field("job_seeker_id")
    private String jobSeekerId;

    @Field("job_id")
    private String jobId;

    @Field("resume_id")
    private String resumeId;

    private String resumeUrl;

    private String message;

    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreatedDate
    private LocalDateTime date;
}
