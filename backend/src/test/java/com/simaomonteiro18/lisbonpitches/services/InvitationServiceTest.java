package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.InvitationStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.exceptions.InvalidGuestException;
import com.simaomonteiro18.lisbonpitches.exceptions.InvitationConflictException;
import com.simaomonteiro18.lisbonpitches.repositories.InvitationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationServiceTest {

    @Mock UserRepository userRepository;

    @Mock ReservationRepository reservationRepository;

    @Mock InvitationRepository invitationRepository;

    @InjectMocks private InvitationService invitationService;

    @Test
    @DisplayName("Teste Positivo")
    void createsInviteWithSuccess() {

        User guest = new User("Simão", "simaomonteiro", "12345", "sm18@gmail.com", "921323536","Sintra");

        User user = new User("Carolina", "carolina", "12345", "carol@gmail.com", "912345678", "Massamá");

        // Os dois setId seguintes são necessários porque, sem eles, ambos os User
        // ficam com id null. O equals() de User compara por id, e null == null,
        // logo dois utilizadores diferentes eram considerados iguais.
        // Isto causava um falso positivo na validação de auto-convite dentro do
        // InvitationService (reservation.getOrganizer().equals(guest)), fazendo
        // o teste falhar mesmo com guest e organizador a serem pessoas distintas.
        guest.setId(1L);
        user.setId(2L);

        Pitch pitch = new Pitch("Real", "Massamá", null, PitchAccess.PUBLIC, PitchType.ELEVEN);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T21:00:00"));

        Invitation invitation = new Invitation(guest, reservation, user);

        when(userRepository.findByUsernameOrEmail(guest.getUsername())).thenReturn(Optional.of(guest));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        when(invitationRepository.existsByGuestAndReservation(guest, reservation)).thenReturn(false);

        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        Invitation invitationFinal = invitationService.createInvitation(2L, guest.getUsername(), 1L);

        assertEquals(InvitationStatus.PENDING, invitationFinal.getStatus());

        assertNotNull(invitationFinal);

        assertEquals(guest, invitationFinal.getGuest());

    }

    @Test
    @DisplayName("Teste de Convite Duplicado")
    void inviteAlreadyExists() {

        User organizer = new User("Simao", "simaomonteiro", "12345", "sm18@gmail.com", "912345678", "Sintra");

        User guest = new User("Lopes", "lopes", "12345", "lopes@gmail.com", "987654321", "Queluz");

        organizer.setId(1L);
        guest.setId(2L);

        Pitch pitch = new Pitch("Jamor", "Oeiras", null, PitchAccess.PUBLIC, PitchType.ELEVEN);

        Reservation reservation = new Reservation(organizer, pitch, Instant.now(), LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T21:00:00"));

        when(userRepository.findByUsernameOrEmail(guest.getUsername())).thenReturn(Optional.of(guest));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        when(invitationRepository.existsByGuestAndReservation(guest, reservation)).thenReturn(true);

        assertThrows(InvitationConflictException.class, () -> {invitationService.createInvitation(1L, guest.getUsername(), 1L);});

    }

    @Test
    @DisplayName("Teste de Auto-Convite")
    void autoInviteDetector() {

        User user = new User("Sara", "sara", "12345", "sara@gmail.com", "943754623", "Setúbal");

        user.setId(1L);

        Pitch pitch = new Pitch("Bonfim", "Setúbal", null, PitchAccess.PUBLIC, PitchType.ELEVEN);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T21:00:00"));

        when(userRepository.findByUsernameOrEmail(user.getUsername())).thenReturn(Optional.of(user));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        when(invitationRepository.existsByGuestAndReservation(user, reservation)).thenReturn(false);

        assertThrows(InvalidGuestException.class, () -> {invitationService.createInvitation(1L, user.getUsername(), 1L);});

    }

}