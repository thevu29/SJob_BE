package org.example.reportservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ReportStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    REJECTED("Rejected");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static ReportStatus fromString(String value) {
        if (value == null) {
            return null;
        }

        for (ReportStatus status : ReportStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
}
