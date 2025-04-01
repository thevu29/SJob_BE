package org.example.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {
    private Integer page;
    private Integer totalPages;
    private Long totalElements;
    private Integer take;
}
