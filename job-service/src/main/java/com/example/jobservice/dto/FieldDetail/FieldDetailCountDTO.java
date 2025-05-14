package com.example.jobservice.dto.FieldDetail;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDetailCountDTO {
    private String id;
    private String name;
    private Long count;
}
