package com.example.recruiterservice.controller;

import com.example.recruiterservice.dto.Invitation.InvitationDTO;
import com.example.recruiterservice.dto.Invitation.request.CreateInvitationRequest;
import com.example.recruiterservice.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @GetMapping
    public String getInvitations() {
        return "List of invitations";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvitationDTO>> createInvitation(@Valid @RequestBody CreateInvitationRequest request) {
        InvitationDTO invitation = invitationService.createInvitation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(invitation, "Gửi lời mời ứng tuyển thành công", HttpStatus.CREATED));
    }
}
