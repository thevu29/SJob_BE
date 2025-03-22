package com.example.recruiterservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "recruiters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("field_id")
    private String fieldId;

    private String name;

    private String about;

    private String image;

    private String website;

    private String address;

    private int members;
}
