package org.example.userservice.entity;

import lombok.*;
import org.example.common.enums.UserRole;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password"})
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private UserRole role;

    private String otp;

    private LocalDateTime otpExpiresAt;

    private boolean otpVerified;

    @Indexed(unique = true)
    @Builder.Default
    private String googleId = null;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field(name = "active")
    @Builder.Default
    private boolean active = true;
}
