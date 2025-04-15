package com.example.jobservice.dto.Field;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldImportDTO {
    private String name;
    private String description;
}
