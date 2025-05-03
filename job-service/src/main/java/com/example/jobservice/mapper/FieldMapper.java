package com.example.jobservice.mapper;

import com.example.jobservice.dto.Field.FieldImportDTO;
import com.example.jobservice.dto.Field.request.CreateFieldRequest;
import com.example.jobservice.dto.Field.request.UpdateFieldRequest;
import com.example.jobservice.entity.Field;
import org.common.dto.Field.FieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FieldMapper {
    FieldDTO toDto(Field field);

    Field toEntity(FieldDTO fieldDTO);

    @Mapping(target = "id", ignore = true)
    Field toEntity(CreateFieldRequest createFieldRequest);

    void updateFieldFromRequest(UpdateFieldRequest updateFieldRequest, @MappingTarget Field field);

    @Mapping(target = "id", ignore = true)
    Field toEntity(FieldImportDTO fieldImportDTO);
}
