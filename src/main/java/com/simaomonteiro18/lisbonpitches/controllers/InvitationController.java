package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.InvitationDTO;
import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.mappers.InvitationMapper;
import com.simaomonteiro18.lisbonpitches.requests.CreateInvitationRequest;
import com.simaomonteiro18.lisbonpitches.services.InvitationService;
import com.simaomonteiro18.lisbonpitches.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @GetMapping
    public ResponseEntity<List<InvitationDTO>> invitationsByReservation(@RequestParam("reservationId") Long reservationId) {

        Long callerId = AuthUtils.getAuthenticatedUserId();

        List<InvitationDTO> list = invitationService.findInvitationsByReservation(callerId, reservationId)
                .stream()
                .map(InvitationMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(list);

    }

    @GetMapping("/me")
    public ResponseEntity<List<InvitationDTO>> myInvitations() {

        Long callerId = AuthUtils.getAuthenticatedUserId();

        List<InvitationDTO> list = invitationService.findInvitationsForGuest(callerId)
                .stream()
                .map(InvitationMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(list);

    }

    @PostMapping
    public ResponseEntity<InvitationDTO> createInvitation(@RequestBody CreateInvitationRequest request) {

        Long userId = AuthUtils.getAuthenticatedUserId();

        Invitation invitation = invitationService.createInvitation(userId, request.identifier(), request.reservationId());

        InvitationDTO invitationDTO = InvitationMapper.toDTO(invitation);

        return ResponseEntity.status(HttpStatus.CREATED).body(invitationDTO);

    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<InvitationDTO> acceptInvitation(@PathVariable Long id) {

        Long callerId = AuthUtils.getAuthenticatedUserId();

        Invitation invitationToAccept = invitationService.acceptInvitation(callerId, id);

        InvitationDTO invitationAcceptedDTO = InvitationMapper.toDTO(invitationToAccept);

        return ResponseEntity.status(HttpStatus.OK).body(invitationAcceptedDTO);

    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<InvitationDTO> rejectInvitation(@PathVariable Long id) {

        Long callerId = AuthUtils.getAuthenticatedUserId();

        Invitation invitationToReject = invitationService.rejectInvitation(callerId, id);

        InvitationDTO invitationRejectedDTO = InvitationMapper.toDTO(invitationToReject);

        return ResponseEntity.status(HttpStatus.OK).body(invitationRejectedDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Long id) {

        Long callerId = AuthUtils.getAuthenticatedUserId();

        invitationService.cancelInvitation(callerId, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

}
