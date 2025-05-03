package org.example.applicationservice.mapper;

import org.example.common.dto.Application.ApplicationDTO;
import org.example.applicationservice.dto.ApplicationCreationDTO;
import org.example.applicationservice.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ApplicationMapper {
    ApplicationDTO toDTO(Application application);

    Application toEntity(ApplicationCreationDTO request);
}
