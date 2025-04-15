package com.example.jobservice.mapper;

import com.example.jobservice.dto.Field.request.UpdateFieldRequest;
import com.example.jobservice.dto.FieldDetail.FieldDetailDTO;
import com.example.jobservice.dto.FieldDetail.request.CreateFieldDetailRequest;
import com.example.jobservice.dto.FieldDetail.request.UpdateFieldDetailRequest;
import com.example.jobservice.entity.Field;
import com.example.jobservice.entity.FieldDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FieldDetailMapper extends BaseMapper {
    @Mapping(target = "fieldId", source = "field", qualifiedByName = "fieldToFieldId")
    FieldDetailDTO toDto(FieldDetail fieldDetail);

    FieldDetail toEntity(FieldDetailDTO fieldDetailDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "field", source = "fieldId", qualifiedByName = "fieldIdToField")
    FieldDetail toEntity(CreateFieldDetailRequest createFieldDetailRequest);


    void updateFieldDetailFromRequest(UpdateFieldDetailRequest updateFieldDetailRequest, @MappingTarget FieldDetail fieldDetail);

}
