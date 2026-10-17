package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.exceptions.*;
import com.simaomonteiro18.lisbonpitches.repositories.InvitationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public Invitation createInvitation(Long callerId, String identifier, Long reservationId) {

        User guest = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new InvalidGuestException("Convidado inválido."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, reservationId));

        boolean isOrganizer = reservation.getOrganizer().getId().equals(callerId);
        boolean isGuestOfReservation = invitationRepository.existsByGuest_IdAndReservation(callerId, reservation);

        if (!isOrganizer && !isGuestOfReservation) {
            throw new UserPermissionException("Não tem permissões para realizar esta ação.");
        }

        if (invitationRepository.existsByGuestAndReservation(guest, reservation)) {
            throw new InvitationConflictException("O utilizador já foi convidado previamente");
        }

        if (reservation.getOrganizer().equals(guest)) {
            throw new InvalidGuestException("O Organizador/a não se pode convidar a ele/a próprio/a");
        }

        Invitation invitation = new Invitation(guest, reservation, userRepository.findById(callerId).orElseThrow(() -> new ResourceNotFoundException(User.class, callerId)));

        return invitationRepository.save(invitation);

    }

    public Invitation acceptInvitation(Long callerId, Long invitationId) {

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException(Invitation.class, invitationId));

        if (!invitation.getGuest().getId().equals(callerId)) {
            throw new UserPermissionException("Não tem permissões para realizar esta ação.");
        }

        invitation.accept();

        return invitationRepository.save(invitation);

    }

    public Invitation rejectInvitation(Long callerId, Long invitationId) {

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException(Invitation.class, invitationId));

        if (!invitation.getGuest().getId().equals(callerId)) {
            throw new UserPermissionException("Não tem permissões para realizar esta ação.");
        }

        invitation.reject();

        return invitationRepository.save(invitation);

    }

    public List<Invitation> findInvitationsByReservation(Long callerId, Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, reservationId));

        boolean isOrganizer = reservation.getOrganizer().getId().equals(callerId);
        boolean isGuest = invitationRepository.existsByGuest_IdAndReservation(callerId, reservation);

        if (!isOrganizer && !isGuest) {
            throw new UserPermissionException("Não tem permissões para realizar esta ação.");
        }

        return invitationRepository.getAllInvitationsByReservation(reservation);

    }

    public List<Invitation> findInvitationsForGuest(Long callerId) {

        return invitationRepository.findByGuest_Id(callerId);

    }

    public void cancelInvitation(Long callerId, Long invitationId) {

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException(Invitation.class, invitationId));

        boolean isInviter = invitation.getInvitedBy().getId().equals(callerId);
        boolean isOrganizer = invitation.getReservation().getOrganizer().getId().equals(callerId);

        if (!isInviter && !isOrganizer) {
            throw new UserPermissionException("Não tem permissões para realizar esta ação.");
        }

        invitationRepository.delete(invitation);

    }

}
