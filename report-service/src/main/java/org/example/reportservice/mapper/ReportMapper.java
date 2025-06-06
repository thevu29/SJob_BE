package org.example.reportservice.mapper;

import org.example.reportservice.dto.CreateReportDTO;
import org.example.reportservice.dto.ReportDTO;
import org.example.reportservice.dto.UpdateReportDTO;
import org.example.reportservice.entity.Report;
import org.example.reportservice.entity.ReportStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {
    ReportDTO toDTO(Report report);

    Report toEntity(CreateReportDTO request);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToReportStatus")
    void toEntity(UpdateReportDTO updateDTO, @MappingTarget Report report);

    @Named("stringToReportStatus")
    default ReportStatus stringToReportStatus(String status) {
        if (status == null) {
            return null;
        }

        try {
            return ReportStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid report status: " + status);
        }
    }
}
