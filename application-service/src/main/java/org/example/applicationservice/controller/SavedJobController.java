package org.example.applicationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.applicationservice.dto.CheckJobSeekerSaveJobDTO;
import org.example.applicationservice.dto.SavedJobCreationDTO;
import org.example.applicationservice.dto.SavedJobDTO;
import org.example.applicationservice.service.SavedJobService;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {
    private final SavedJobService savedJobService;

    @GetMapping("/job/job-seeker")
    public ResponseEntity<ApiResponse<SavedJobDTO>> getSavedJobByJobIdAndJobSeekerId(
            @RequestParam String jobId,
            @RequestParam String jobSeekerId
    ) {
        SavedJobDTO savedJob = savedJobService.getSavedJobByJobIdAndJobSeekerId(jobId, jobSeekerId);
        return ResponseEntity.ok(ApiResponse.success(savedJob, "Lấy việc làm đã lưu thành công"));
    }

    @PostMapping("/check-save")
    public ResponseEntity<ApiResponse<Boolean>> checkJobSeekerSaveJob(@Valid @RequestBody CheckJobSeekerSaveJobDTO request) {
        boolean hasSaved = savedJobService.hasJobSeekerSavedJob(request);
        return ResponseEntity.ok(ApiResponse.success(hasSaved, "Kiểm tra việc làm đã lưu thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SavedJobDTO>> saveJob(@Valid @RequestBody SavedJobCreationDTO request) {
        SavedJobDTO savedJob = savedJobService.saveJob(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedJob, "Lưu việc làm thành công", HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(@PathVariable String id) {
        savedJobService.unSaveJob(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Bỏ lưu việc làm thành công", HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedJobDTO>>> getPaginatedSavedJobs(
            @RequestParam(value = "jobSeekerId", required = false) String jobSeekerId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<SavedJobDTO> pages = savedJobService.getPaginatedSavedJobs(jobSeekerId, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách các việc làm đã lưu thành công"));
    }
}
