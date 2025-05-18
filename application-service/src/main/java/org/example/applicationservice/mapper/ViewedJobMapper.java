package org.example.applicationservice.mapper;

import org.example.applicationservice.dto.ViewedJobCreationDTO;
import org.example.applicationservice.dto.ViewedJobDTO;
import org.example.applicationservice.entity.ViewedJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ViewedJobMapper {
    @Mapping(source = "jobId", target = "job.id")
    @Mapping(target = "job", ignore = true)
    ViewedJobDTO toDTO(ViewedJob viewedJob);

    ViewedJob toEntity(ViewedJobCreationDTO request);
}
