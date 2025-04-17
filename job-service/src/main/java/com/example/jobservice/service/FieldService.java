package com.example.jobservice.service;

import com.example.jobservice.dto.Field.FieldImportDTO;
import com.example.jobservice.dto.Field.request.CreateFieldRequest;
import com.example.jobservice.dto.Field.request.UpdateFieldRequest;
import com.example.jobservice.entity.Field;
import com.example.jobservice.mapper.FieldMapper;
import com.example.jobservice.repository.FieldRepository;
import com.example.jobservice.utils.helpers.CSVHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.common.dto.Field.FieldDTO;
import org.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class FieldService {
    private final FieldRepository fieldRepository;
    private final FieldMapper fieldMapper;
    private final CSVHelper csvHelper;

    public List<FieldDTO> getFields() {
        List<Field> fields = fieldRepository.findAll();

        return fields.stream()
                .map(fieldMapper::toDto)
                .collect(Collectors.toList());
    }

    public FieldDTO getField(String fieldId) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Ngành nghề/Lĩnh vực với id: " + fieldId));
        return fieldMapper.toDto(field);
    }

    public FieldDTO createField(CreateFieldRequest createFieldRequest) {
        Field field = fieldMapper.toEntity(createFieldRequest);
        Field savedField = fieldRepository.save(field);
        return fieldMapper.toDto(savedField);
    }

    public FieldDTO updateField(UpdateFieldRequest updateFieldRequest, String fieldId) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Ngành nghề/Lĩnh vực với id: " + fieldId));
        fieldMapper.updateFieldFromRequest(updateFieldRequest, field);
        Field updatedField = fieldRepository.save(field);
        return fieldMapper.toDto(updatedField);
    }

    public void deleteField(String fieldId) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Ngành nghề/Lĩnh vực với id: " + fieldId));
        fieldRepository.delete(field);

    }

    public void importCSVFile(MultipartFile file) {
        try {
            // Validate file
            csvHelper.validateCSVFile(file);

            // Parse CSV to DTO
            List<FieldImportDTO> fieldDTOs = CSVHelper.csvToFieldImportDTOs(file.getInputStream());

            // Convert to entities and save
            List<Field> saveFields = fieldDTOs.stream()
                    .map(fieldMapper::toEntity)
                    .map(fieldRepository::save)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Nhập file thất bại: " + e.getMessage());
        }
    }

    public List<FieldDTO> getFieldsByNames(List<String> names) {
        List<Field> fields = fieldRepository.findByNameIn(names);
        return fields.stream()
                .map(fieldMapper::toDto)
                .collect(Collectors.toList());
    }
}
