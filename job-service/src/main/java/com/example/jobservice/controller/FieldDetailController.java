package com.example.jobservice.controller;

import com.example.jobservice.dto.FieldDetail.request.CreateFieldDetailRequest;
import com.example.jobservice.dto.FieldDetail.request.UpdateFieldDetailRequest;
import com.example.jobservice.service.FieldDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.FieldDetail.FieldDetailDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/field-details")
@RequiredArgsConstructor
public class FieldDetailController {
    private final FieldDetailService fieldDetailService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FieldDetailDTO>>> getFieldDetails() {
        List<FieldDetailDTO> fieldDetailDTOList = fieldDetailService.getFieldDetails();
        return ResponseEntity.ok(
                ApiResponse.success(fieldDetailDTOList, "Lấy danh sách chi tiết các Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FieldDetailDTO>> getFieldDetail(@PathVariable String id) {
        FieldDetailDTO fieldDetailDTO = fieldDetailService.getFieldDetail(id);
        return ResponseEntity.ok(
                ApiResponse.success(fieldDetailDTO, "Lấy chi tiết các Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FieldDetailDTO>> createFieldDetail(@Valid @RequestBody CreateFieldDetailRequest createFieldDetailRequest) {
        FieldDetailDTO fieldDetailDTO = fieldDetailService.createFieldDetail(createFieldDetailRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(fieldDetailDTO, "Tạo chi tiết Ngành nghề/Lĩnh vực thành công", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FieldDetailDTO>> updateFieldDetail(@Valid @RequestBody UpdateFieldDetailRequest updateFieldDetailRequest, @PathVariable String id) {
        FieldDetailDTO fieldDetailDTO = fieldDetailService.updateFieldDetail(updateFieldDetailRequest, id);
        return ResponseEntity.ok(
                ApiResponse.success(fieldDetailDTO, "Cập nhật chi tiết Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteFieldDetail(@PathVariable String id) {
        fieldDetailService.deleteFieldDetail(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa chi tiết Ngành nghề/Lĩnh vực thành công", HttpStatus.OK)
        );
    }

    @PostMapping(value = "/import")
    public ResponseEntity<ApiResponse<?>> importFile(@RequestParam("file") @Valid MultipartFile file) {
        fieldDetailService.importFileCSV(file);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Nhập file thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/by-names")
    public ResponseEntity<ApiResponse<List<FieldDetailDTO>>> getFieldDetailsByNames(@RequestBody List<String> names) {
        List<FieldDetailDTO> fieldDetails = fieldDetailService.getFieldDetailsByNames(names);
        return ResponseEntity.ok(
                ApiResponse.success(fieldDetails, "Lấy danh sách ngành nghề theo tên thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<ApiResponse<List<FieldDetailDTO>>> getFieldDetailsByJobId(@PathVariable String jobId) {
        List<FieldDetailDTO> fieldDetails = fieldDetailService.getFieldDetailsByJobId(jobId);
        return ResponseEntity.ok(
                ApiResponse.success(fieldDetails, "Lấy danh sách chi tiết ngành nghề theo việc làm thành công", HttpStatus.OK)
        );
    }

}
