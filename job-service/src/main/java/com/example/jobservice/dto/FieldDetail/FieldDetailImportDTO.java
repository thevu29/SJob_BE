package com.example.jobservice.dto.FieldDetail;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDetailImportDTO {
    private String field;
    private String fieldDetail;
}
