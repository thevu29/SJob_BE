package com.example.jobservice.service;

import com.example.jobservice.dto.FieldDetail.FieldDetailDTO;
import com.example.jobservice.dto.FieldDetail.FieldDetailImportDTO;
import com.example.jobservice.dto.FieldDetail.request.CreateFieldDetailRequest;
import com.example.jobservice.dto.FieldDetail.request.UpdateFieldDetailRequest;
import com.example.jobservice.entity.Field;
import com.example.jobservice.entity.FieldDetail;
import com.example.jobservice.exception.FileUploadException;
import com.example.jobservice.exception.ResourceNotFoundException;
import com.example.jobservice.mapper.FieldDetailMapper;
import com.example.jobservice.repository.FieldDetailRepository;
import com.example.jobservice.repository.FieldRepository;
import com.example.jobservice.utils.helpers.CSVHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldDetailService {
    private final FieldDetailRepository fieldDetailRepository;
    private final FieldRepository fieldRepository;
    private final FieldDetailMapper fieldDetailMapper;
    private final CSVHelper csvHelper;

    public List<FieldDetailDTO> getFieldDetails() {
        List<FieldDetail> fields = fieldDetailRepository.findAll();

        return fields.stream()
                .map(fieldDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    public FieldDetailDTO getFieldDetail(String id) {
        FieldDetail fieldDetail = fieldDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết Ngành nghề/Lĩnh vực với id: " + id));
        return fieldDetailMapper.toDto(fieldDetail);
    }


    public FieldDetailDTO createFieldDetail(CreateFieldDetailRequest createFieldDetailRequest) {

        FieldDetail fieldDetail = fieldDetailMapper.toEntity(createFieldDetailRequest);

        FieldDetail savedFieldDetail = fieldDetailRepository.save(fieldDetail);
        return fieldDetailMapper.toDto(savedFieldDetail);
    }

    public FieldDetailDTO updateFieldDetail(UpdateFieldDetailRequest updateFieldDetailRequest, String fieldDetailId) {
        FieldDetail fieldDetail = fieldDetailRepository.findById(fieldDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết Ngành nghề/Lĩnh vực với id: " + fieldDetailId));
        fieldDetailMapper.updateFieldDetailFromRequest(updateFieldDetailRequest, fieldDetail);
        FieldDetail updatedFieldDetail = fieldDetailRepository.save(fieldDetail);
        return fieldDetailMapper.toDto(updatedFieldDetail);
    }

    public void deleteFieldDetail(String id) {
        FieldDetail fieldDetail = fieldDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết Ngành nghề/Lĩnh vực với id: " + id));
        fieldDetailRepository.delete(fieldDetail);
    }

    public void importFileCSV(MultipartFile file) {
        try {
            // Validate file
            csvHelper.validateCSVFile(file);

            // Parse CSV to DTO
            List<FieldDetailImportDTO> fieldDetailImportDTOS = CSVHelper.csvToFieldDetailImportDTOs(file.getInputStream());

            // 1. Extract unique field names
            Set<String> uniqueFieldNames = fieldDetailImportDTOS.stream()
                    .map(FieldDetailImportDTO::getField)
                    .collect(Collectors.toSet());

            // 2. Fetch all required fields in one query
            Map<String, Field> fieldMap = fieldRepository.findByNameIn(uniqueFieldNames)
                    .stream()
                    .collect(Collectors.toMap(Field::getName, field -> field));

            // 3. Validate all fields exist
            Set<String> missingFields = uniqueFieldNames.stream()
                    .filter(name -> !fieldMap.containsKey(name))
                    .collect(Collectors.toSet());
            if (!missingFields.isEmpty()) {
                throw new FileUploadException("Không tìm thấy các ngành nghề: " + String.join(", ", missingFields));
            }

            // 4. Create and save field detail
            List<FieldDetail> fieldDetails = fieldDetailImportDTOS.stream()
                    .map(fieldDetailImportDTO -> {
                        Field field = fieldMap.get(fieldDetailImportDTO.getField());
                        return FieldDetail.builder()
                                .field(field)
                                .name(fieldDetailImportDTO.getFieldDetail())
                                .build();
                    })
                    .collect(Collectors.toList());

            fieldDetailRepository.saveAll(fieldDetails);


        } catch (IOException e) {
            throw new RuntimeException("Nhập file thất bại: " + e.getMessage());
        }
    }

    public List<FieldDetailDTO> getFieldDetailsByNames(List<String> names) {
        List<FieldDetail> fieldDetails = fieldDetailRepository.findByNameIn(names);
        return fieldDetails.stream()
                .map(fieldDetailMapper::toDto)
                .collect(Collectors.toList());
    }
}
