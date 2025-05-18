package org.example.applicationservice.mapper;

import org.example.applicationservice.dto.SavedJobCreationDTO;
import org.example.applicationservice.dto.SavedJobDTO;
import org.example.applicationservice.entity.SavedJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SavedJobMapper {
    @Mapping(source = "jobId", target = "job.id")
    @Mapping(target = "job", ignore = true)
    SavedJobDTO toDTO(SavedJob savedJob);

    SavedJob toEntity(SavedJobCreationDTO request);
}
