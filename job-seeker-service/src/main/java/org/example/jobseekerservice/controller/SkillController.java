package org.example.jobseekerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.dto.Skill.SkillDTO;
import org.example.jobseekerservice.dto.Skill.request.CreateSkillRequest;
import org.example.jobseekerservice.dto.Skill.request.UpdateSkillRequest;
import org.example.jobseekerservice.dto.response.ApiResponse;
import org.example.jobseekerservice.service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillDTO>>> getSkills() {
        List<SkillDTO> skills = skillService.getAllSkills();

        return ResponseEntity.ok(
                ApiResponse.success(skills, "Skills fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("job-seeker/{jobSeekerId}")
    public ResponseEntity<ApiResponse<List<SkillDTO>>> getJobSeekerSkills(@PathVariable String jobSeekerId) {
        List<SkillDTO> skills = skillService.getJobSeekerSkills(jobSeekerId);

        return ResponseEntity.ok(
                ApiResponse.success(skills, "Skill fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillDTO>> getSkillById(@PathVariable String id) {
        SkillDTO skill = skillService.getSkillById(id);

        return ResponseEntity.ok(
                ApiResponse.success(skill, "Skill fetched successfully", HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SkillDTO>> createSkill(@Valid @RequestBody CreateSkillRequest request) {
        SkillDTO createdSkill = skillService.createSkill(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        createdSkill, "Skill created successfully", HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillDTO>> updateSkill(
            @PathVariable String id,
            @Valid @RequestBody UpdateSkillRequest request
    ) {
        SkillDTO updatedSkill = skillService.updateSkill(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(updatedSkill, "Skill updated successfully", HttpStatus.OK)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable String id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Skill deleted successfully", HttpStatus.OK)
        );
    }
}
