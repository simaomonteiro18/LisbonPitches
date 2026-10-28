package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.exceptions.InvalidTimeException;
import com.simaomonteiro18.lisbonpitches.exceptions.PitchNotBookableException;
import com.simaomonteiro18.lisbonpitches.exceptions.ReservationConflictException;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock ReservationRepository reservationRepository;

    @Mock UserRepository userRepository;

    @Mock PitchRepository pitchRepository;

    @Mock AvailabilityService availabilityService;

    @InjectMocks private ReservationService reservationService;

    @Test
    @DisplayName("Teste de Duração Negativa")
    void invertedTime() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T21:00:00"), LocalDateTime.parse("2026-09-04T20:00:00"));});

    }

    @Test
    @DisplayName("Teste de Tempo < 1 hora")
    void insufficientReservationTime() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T20:30:00"));});

    }

    @Test
    @DisplayName("Teste de Durações não múltiplas de 60")
    void nonMultipleOf60MinutesDuration() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T21:30:00"));});

    }

    @Test
    @DisplayName("Teste de Sobreposição de Reservas")
    void overlappingReservation() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(reservationRepository.existsOverlappingReservation(pitch, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"))).thenReturn(true);

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of(20, 21)));

        assertThrows(ReservationConflictException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste Positivo de Reserva")
    void createsReservationSuccessfully() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-05T20:00:00"), LocalDateTime.parse("2026-09-05T22:00:00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(reservationRepository.existsOverlappingReservation(pitch, LocalDateTime.parse("2026-09-05T20:00:00"), LocalDateTime.parse("2026-09-05T22:00:00"))).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 5)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of(20, 21)));

        Reservation resultado = reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-05T20:00:00"), LocalDateTime.parse("2026-09-05T22:00:00"));

        assertNotNull(resultado);
        assertEquals(LocalDateTime.parse("2026-09-05T20:00:00"), resultado.getStartTime());
        assertEquals(LocalDateTime.parse("2026-09-05T22:00:00"), resultado.getEndTime());

    }

    @Test
    @DisplayName("Teste de reserva que não começa a hora em ponto")
    void startTimeNotOnTheHour() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T19:30:00"), LocalDateTime.parse("2026-09-04T20:30:00"));});

    }

    @Test
    @DisplayName("Teste de reserva em horas ocupadas")
    void requestedHourMissingFromFreeHours() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of(20)));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste de reserva em campo não reservável")
    void reservationOnNonReservablePitch() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.NOT_BOOKABLE, List.of()));

        assertThrows(PitchNotBookableException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste de reserva em campo sem horário definido")
    void reservationOnPitchWithoutSchedule() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.NO_SCHEDULE, List.of()));

        assertThrows(PitchNotBookableException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste de reserva com a primeira hora ocupada")
    void firstRequestedHourMissingFromFreeHours() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of(21)));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste de reserva com todas as horas ocupadas")
    void allRequestedHoursOccupied() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of()));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste de reserva com final após a meia-noite")
    void reservationEndingAfterNextMidnight() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of(23)));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T23:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"));});

    }

}
