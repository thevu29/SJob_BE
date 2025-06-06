package org.example.reportservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.ApiResponse;
import org.example.reportservice.dto.CreateReportDTO;
import org.example.reportservice.dto.ReportDTO;
import org.example.reportservice.dto.UpdateReportDTO;
import org.example.reportservice.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportDTO>>> getPaginatedReports(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<ReportDTO> pages = reportService.getPaginatedReports(query, status, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy báo cáo thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO>> getReportById(@PathVariable String id) {
        ReportDTO report = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success(report, "Lấy báo cáo thành công", HttpStatus.OK));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReportDTO>> createReport(@Valid @ModelAttribute CreateReportDTO request) {
        ReportDTO report = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(report, "Báo cáo thành công", HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO>> updateReport(
            @PathVariable String id,
            @Valid @RequestBody UpdateReportDTO request
    ) {
        ReportDTO report = reportService.updateReport(id, request);
        return ResponseEntity.ok(ApiResponse.success(report, "Cập nhật báo cáo thành công", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoá báo cáo thành công", HttpStatus.OK));
    }
}
