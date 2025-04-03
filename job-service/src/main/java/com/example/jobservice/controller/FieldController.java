package com.example.jobservice.controller;


import com.example.jobservice.dto.Field.FieldDTO;
import com.example.jobservice.dto.Field.request.CreateFieldRequest;
import com.example.jobservice.dto.Field.request.UpdateFieldRequest;
import com.example.jobservice.dto.response.ApiResponse;
import com.example.jobservice.service.FieldService;
import com.example.jobservice.utils.helpers.CSVHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {
    private final FieldService fieldService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FieldDTO>>> getFields() {
        List<FieldDTO> fieldDTOList = fieldService.getFields();
        return ResponseEntity.ok(
                ApiResponse.success(fieldDTOList, "Lấy danh sách Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FieldDTO>> getField(@PathVariable String id) {
        FieldDTO fieldDTO = fieldService.getField(id);
        return ResponseEntity.ok(
                ApiResponse.success(fieldDTO, "Lấy chi tiết Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FieldDTO>> createField(@Valid @RequestBody CreateFieldRequest createFieldRequest) {
        FieldDTO fieldDTO = fieldService.createField(createFieldRequest);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(ApiResponse.success(fieldDTO, "Tạo Ngành nghề/Lĩnh vực thành công", HttpStatus.CREATED));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FieldDTO>> updateField(@Valid @RequestBody UpdateFieldRequest updateFieldRequest, @PathVariable String id) {
        FieldDTO fieldDTO = fieldService.updateField(updateFieldRequest, id);
        return ResponseEntity.ok(
                ApiResponse.success(fieldDTO, "Cập nhật Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteField(@PathVariable String id) {
        fieldService.deleteField(id);
        return ResponseEntity.ok(
                ApiResponse.success(null,"Xóa Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<?>> importFile(@RequestParam("file") MultipartFile file) {
        fieldService.importCSVFile(file);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Nhập file thành công", HttpStatus.OK)
        );
    }
}
