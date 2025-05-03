package org.common.dto.NotificationPreference;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceCreateDTO {
    @NotBlank(message = "Không được để trống userId")
    private String userId;
}
