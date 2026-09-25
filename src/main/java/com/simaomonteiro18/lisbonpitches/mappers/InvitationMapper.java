package com.simaomonteiro18.lisbonpitches.mappers;

import com.simaomonteiro18.lisbonpitches.dtos.InvitationDTO;
import com.simaomonteiro18.lisbonpitches.entities.Invitation;

public class InvitationMapper {

    public static InvitationDTO toDTO(Invitation invitation) {

        InvitationDTO invitationDTO = new InvitationDTO(invitation.getId(), invitation.getGuest().getId(), invitation.getGuest().getName(), invitation.getInvitedBy().getId(), invitation.getReservation().getId(), invitation.getStatus());

        return invitationDTO;

    }

}
