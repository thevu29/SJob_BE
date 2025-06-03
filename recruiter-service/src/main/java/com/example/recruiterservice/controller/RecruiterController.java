package com.example.recruiterservice.controller;

import com.example.recruiterservice.dto.Recruiter.GetRecruiterStatisticsDTO;
import com.example.recruiterservice.dto.Recruiter.request.UpdateRecruiterRequest;
import com.example.recruiterservice.service.RecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.common.dto.Recruiter.RecruiterDTO;
import org.example.common.dto.Recruiter.RecruiterWithUserDTO;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/recruiters")
@RequiredArgsConstructor
public class RecruiterController {
    private final RecruiterService recruiterService;

    @GetMapping("/count-in-month")
    public ResponseEntity<ApiResponse<GetRecruiterStatisticsDTO>> getRecruiterCountInMonth() {
        GetRecruiterStatisticsDTO stats = recruiterService.getRecruiterCountInMonth();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy số lượng nhà tuyển dụng trong tháng thành công"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<GetRecruiterStatisticsDTO>>> getRecruiterStatistics() {
        List<GetRecruiterStatisticsDTO> statistics = recruiterService.getRecruiterStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics, "Lấy thống kê nhà tuyển dụng thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecruiterWithUserDTO>>> getRecruiters(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<RecruiterWithUserDTO> pages = recruiterService.findPagedRecruiters(
                query,
                active,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách nhà tuyển dụng thành công"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RecruiterWithUserDTO>>> getAllRecruiters() {
        List<RecruiterWithUserDTO> recruiters = recruiterService.getAllRecruiters();
        return ResponseEntity.ok(
                ApiResponse.success(recruiters, "Lấy danh sách nhà tuyển dụng thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> getRecruiterById(@PathVariable String id) {
        RecruiterWithUserDTO recruiter = recruiterService.getRecruiterById(id);
        return ResponseEntity.ok(
                ApiResponse.success(recruiter, "Lấy chi tiết nhà tuyển dụng thành công", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> createRecruiter(@Valid @RequestBody RecruiterCreationDTO request) {
        RecruiterWithUserDTO recruiter = recruiterService.createRecruiter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(recruiter, "Tạo nhà tuyển dụng thành công", HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> updateRecruiter(
            @PathVariable String id,
            @Valid @ModelAttribute UpdateRecruiterRequest request
    ) {
        RecruiterWithUserDTO recruiter = recruiterService.updateRecruiter(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(recruiter, "Cập nhật nhà tuyển dụng thành công", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRecruiter(@PathVariable String id) {
        recruiterService.deleteRecruiter(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa nhà tuyển dụng thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/exists/{id}")
    public boolean checkIfRecruiterExists(@PathVariable String id) {
        return recruiterService.checkIfRecruiterExists(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> getRecruiterByUserId(@PathVariable String userId) {
        RecruiterWithUserDTO recruiter = recruiterService.getRecruiterByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.success(recruiter, "Lấy thông tin nhà tuyển dụng thành công", HttpStatus.OK)
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<RecruiterWithUserDTO>> getRecruiterByEmail(@PathVariable String email) {
        RecruiterWithUserDTO recruiter = recruiterService.getRecruiterByEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success(recruiter, "Lấy thông tin nhà tuyển dụng theo email thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<?>> importFile(@RequestParam("file") MultipartFile file) {
        recruiterService.importFileCSV(file);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Nhập file thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/by-names")
    public ResponseEntity<ApiResponse<List<RecruiterDTO>>> getRecruitersByNames(@RequestBody List<String> names) {
        List<RecruiterDTO> recruiters = recruiterService.getRecruitersByNames(names);
        return ResponseEntity.ok(
                ApiResponse.success(recruiters, "Lấy danh sách nhà tuyển dụng theo tên thành công", HttpStatus.OK)
        );
    }

    @PostMapping("/ids")
    public ResponseEntity<ApiResponse<List<RecruiterDTO>>> getRecruitersByIds(@RequestBody List<String> ids) {
        List<RecruiterDTO> recruiters = recruiterService.getRecruitersIds(ids);
        return ResponseEntity.ok(
                ApiResponse.success(recruiters, "Lấy danh sách nhà tuyển dụng theo id thành công", HttpStatus.OK)
        );
    }
}
