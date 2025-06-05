package com.example.recruiterservice.controller;

import com.example.recruiterservice.dto.Invitation.InvitationDTO;
import com.example.recruiterservice.dto.Invitation.request.CreateInvitationRequest;
import com.example.recruiterservice.entity.Invitation.InvitationStatus;
import com.example.recruiterservice.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvitationDTO>> createInvitation(@Valid @RequestBody CreateInvitationRequest request) {
        InvitationDTO invitation = invitationService.createInvitation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(invitation, "Gửi lời mời ứng tuyển thành công", HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvitation(@PathVariable("id") String id) {
        invitationService.deleteInvitation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa lời mời ứng tuyển thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvitationDTO>>> getInvitations(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "status", required = false) InvitationStatus status,
            @RequestParam(value = "recruiterId", required = false) String recruiterId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<InvitationDTO> pages = invitationService.findInvitations(
                query,
                status,
                recruiterId,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách lời mời thành công"));
    }
}
