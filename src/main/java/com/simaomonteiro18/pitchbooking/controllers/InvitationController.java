package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.dtos.InvitationDTO;
import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.mappers.InvitationMapper;
import com.simaomonteiro18.pitchbooking.requests.CreateInvitationRequest;
import com.simaomonteiro18.pitchbooking.services.InvitationService;
import com.simaomonteiro18.pitchbooking.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping
    public ResponseEntity<InvitationDTO> createInvitation(@RequestBody CreateInvitationRequest request) {

        Long userId = AuthUtils.getAuthenticatedUserId();

        Invitation invitation = invitationService.createInvitation(userId, request.reservationId());

        InvitationDTO invitationDTO = InvitationMapper.toDTO(invitation);

        return ResponseEntity.status(HttpStatus.CREATED).body(invitationDTO);

    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<InvitationDTO> acceptInvitation(@PathVariable Long id) {

        Invitation invitationToAccept = invitationService.acceptInvitation(id);

        InvitationDTO invitationAcceptedDTO = InvitationMapper.toDTO(invitationToAccept);

        return ResponseEntity.status(HttpStatus.OK).body(invitationAcceptedDTO);

    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<InvitationDTO> rejectInvitation(@PathVariable Long id) {

        Invitation invitationToReject = invitationService.rejectInvitation(id);

        InvitationDTO invitationRejectedDTO = InvitationMapper.toDTO(invitationToReject);

        return ResponseEntity.status(HttpStatus.OK).body(invitationRejectedDTO);

    }
    
}
