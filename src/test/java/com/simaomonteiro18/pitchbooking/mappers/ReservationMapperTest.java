package com.simaomonteiro18.pitchbooking.mappers;

import com.simaomonteiro18.pitchbooking.dtos.ReservationDTO;
import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationMapperTest {

    User organizer = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");
    User u1 = new User("Mafalda", "12345", "mf@gmail.com", "987654321", "Lisboa");
    User u2 = new User("Diogo", "12345", "dl@gmail.com", "912321324", "Queluz");

    Pitch pitch = new Pitch("Sintrense", "Sintra", BigDecimal.valueOf(15.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

    Reservation reservation = new Reservation(organizer, pitch, Instant.now(), LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T21:00:00"));

    Invitation i1 = new Invitation(u1, reservation);
    Invitation i2 = new Invitation(u2, reservation);

    @Test
    @DisplayName("Teste de Conversão para DTO")
    void convertsToDTOSuccessfully() {

        i1.accept();
        i2.accept();

        pitch.setId(1L);

        reservation.getInviteList().add(i1);
        reservation.getInviteList().add(i2);

        ReservationDTO reservationDTO = ReservationMapper.toDTO(reservation);

        assertEquals(reservation.getId(), reservationDTO.getId());
        assertEquals(reservation.getOrganizer().getName(), reservationDTO.getOrganizerName());
        assertEquals(reservation.getPitch().getId(), reservationDTO.getPitch().getId());
        assertEquals(reservation.getPitch().getName(), reservationDTO.getPitch().getName());
        assertEquals(reservation.getPitch().getCity(), reservationDTO.getPitch().getCity());
        assertEquals(reservation.getPitch().getPricePerHour(), reservationDTO.getPitch().getPricePerHour());
        assertEquals(reservation.getPitch().getPitchType(), reservationDTO.getPitch().getType());
        assertEquals(reservation.getMoment(), reservationDTO.getMoment());
        assertEquals(reservation.getStartTime(), reservationDTO.getStartTime());
        assertEquals(reservation.getEndTime(), reservationDTO.getEndTime());
        assertEquals(reservation.pricePerPerson(), reservationDTO.getPricePerPerson());
        assertEquals(reservation.invitesAccepted(), reservationDTO.getInvitesAccepted());

    }

}
