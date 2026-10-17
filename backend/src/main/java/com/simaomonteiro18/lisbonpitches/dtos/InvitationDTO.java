package com.simaomonteiro18.lisbonpitches.dtos;

import com.simaomonteiro18.lisbonpitches.entities.enums.InvitationStatus;

public class InvitationDTO {

    private Long id;
    private Long guestId;
    private String guestName;
    private Long invitedById;
    private Long reservationId;
    private InvitationStatus status;

    public InvitationDTO() {
    }

    public InvitationDTO(Long id, Long guestId, String guestName, Long invitedById, Long reservationId, InvitationStatus status) {
        this.id = id;
        this.guestId = guestId;
        this.guestName = guestName;
        this.invitedById = invitedById;
        this.reservationId = reservationId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getGuestId() {
        return guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public Long getInvitedById() {
        return invitedById;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public InvitationStatus getStatus() {
        return status;
    }
    
}
