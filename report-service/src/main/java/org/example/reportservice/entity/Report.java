package org.example.reportservice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    private String id;

    @Field("job_seeker_id")
    private String jobSeekerId;

    @Field("recruiter_id")
    private String recruiterId;

    private String message;

    private String reason;

    private String evidence;

    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @CreatedDate
    @Field("date")
    private LocalDateTime date;
}
