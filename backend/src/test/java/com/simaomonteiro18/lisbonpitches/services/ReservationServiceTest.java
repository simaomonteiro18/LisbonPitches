package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.dtos.SlotDTO;
import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.InvitationStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.exceptions.*;
import com.simaomonteiro18.lisbonpitches.repositories.InvitationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock ReservationRepository reservationRepository;

    @Mock UserRepository userRepository;

    @Mock PitchRepository pitchRepository;

    @Mock AvailabilityService availabilityService;

    @Mock InvitationRepository invitationRepository;

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
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T20:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-04T21:00:00"), true)), 60));

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
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-05T20:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-05T21:00:00"), true)), 60));

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

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T20:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-04T21:00:00"), true)), 60));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 3)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.NO_SCHEDULE));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T19:30:00"), LocalDateTime.parse("2026-09-04T20:30:00"));});

    }

    @Test
    @DisplayName("Teste de reserva em horas ocupadas")
    void rejectsReservationWhenALaterSlotIsOccupied() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T20:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-04T21:00:00"), false)), 60));

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

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.NOT_BOOKABLE));

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

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4))).thenReturn(new AvailabilityDTO(AvailabilityStatus.NO_SCHEDULE));

        assertThrows(PitchNotBookableException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T22:00:00"));});

    }

    @Test
    @DisplayName("Teste de reserva com a primeira hora ocupada")
    void rejectsReservationWhenTheFirstSlotIsOccupied() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T20:00:00"), false),
                                new SlotDTO(LocalDateTime.parse("2026-09-04T21:00:00"), true)), 60));

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

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T20:00:00"), false),
                                new SlotDTO(LocalDateTime.parse("2026-09-04T21:00:00"), false)), 60));

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

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T23:00:00"), true)), 60));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T23:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"));});

    }

    @Test
    void acceptsThirtyMinuteReservationWhenPitchSlotIsThirty() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);
        pitch.setSlotMinutes(30);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-05T20:00:00"), LocalDateTime.parse("2026-09-05T20:30:00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(reservationRepository.existsOverlappingReservation(pitch, LocalDateTime.parse("2026-09-05T20:00:00"), LocalDateTime.parse("2026-09-05T20:30:00"))).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 5)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-05T20:00:00"), true)), 30));

        Reservation resultado = reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-05T20:00:00"), LocalDateTime.parse("2026-09-05T20:30:00"));

        assertNotNull(resultado);
        assertEquals(LocalDateTime.parse("2026-09-05T20:00:00"), resultado.getStartTime());
        assertEquals(LocalDateTime.parse("2026-09-05T20:30:00"), resultado.getEndTime());

    }

    @Test
    void rejectsDurationThatIsNotMultipleOfThePitchSlot() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);
        pitch.setSlotMinutes(30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T20:00:00"), LocalDateTime.parse("2026-09-04T20:45:00"));});

    }

    @Test
    void acceptsReservationStartingOnTheHalfHourWhenPitchSlotIsThirty() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);
        pitch.setSlotMinutes(30);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-05T20:30:00"), LocalDateTime.parse("2026-09-05T21:00:00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(reservationRepository.existsOverlappingReservation(pitch, LocalDateTime.parse("2026-09-05T20:30:00"), LocalDateTime.parse("2026-09-05T21:00:00"))).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 5)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-05T20:30:00"), true)), 30));

        Reservation resultado = reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-05T20:30:00"), LocalDateTime.parse("2026-09-05T21:00:00"));

        assertNotNull(resultado);
        assertEquals(LocalDateTime.parse("2026-09-05T20:30:00"), resultado.getStartTime());
        assertEquals(LocalDateTime.parse("2026-09-05T21:00:00"), resultado.getEndTime());

    }

    @Test
    void rejectsReservationThatGoesBeyondTheLastSlot() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T20:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-04T21:00:00"), true)), 60));

        assertThrows(InvalidTimeException.class, () -> {reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T21:00:00"), LocalDateTime.parse("2026-09-04T23:00:00"));});

    }

    @Test
    void acceptsReservationCrossingMidnightWhenScheduleAllowsIt() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-04T23:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(reservationRepository.existsOverlappingReservation(pitch, LocalDateTime.parse("2026-09-04T23:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"))).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T23:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-05T00:00:00"), true)), 60));

        Reservation resultado = reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-04T23:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"));

        assertNotNull(resultado);
        assertEquals(LocalDateTime.parse("2026-09-04T23:00:00"), resultado.getStartTime());
        assertEquals(LocalDateTime.parse("2026-09-05T01:00:00"), resultado.getEndTime());

    }

    @Test
    void acceptsReservationStartingAfterMidnightUsingPreviousDaySchedule() {

        User user = new User("Simão", "12345", "sm18@gmail.com", "912345678", "Sintra");

        user.setId(1L);

        Pitch pitch = new Pitch("Real", "Massamá", BigDecimal.valueOf(25.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

        pitch.setId(1L);

        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-09-05T00:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(pitchRepository.findWithLockById(1L)).thenReturn(Optional.of(pitch));

        when(reservationRepository.existsOverlappingReservation(pitch, LocalDateTime.parse("2026-09-05T00:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"))).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 5)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-05T20:00:00"), true)), 60));

        when(availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 9, 4)))
                .thenReturn(new AvailabilityDTO(AvailabilityStatus.BOOKABLE,
                        List.of(new SlotDTO(LocalDateTime.parse("2026-09-04T23:00:00"), true),
                                new SlotDTO(LocalDateTime.parse("2026-09-05T00:00:00"), true)), 60));

        Reservation resultado = reservationService.createReservation(1L, 1L, LocalDateTime.parse("2026-09-05T00:00:00"), LocalDateTime.parse("2026-09-05T01:00:00"));

        assertNotNull(resultado);
        assertEquals(LocalDateTime.parse("2026-09-05T00:00:00"), resultado.getStartTime());
        assertEquals(LocalDateTime.parse("2026-09-05T01:00:00"), resultado.getEndTime());

    }

    @Test
    @DisplayName("Teste aos id's de reservas que o utilizador organizou")
    void testIdsOfReservationsOrganized() throws Exception {

        assertThrows(UserPermissionException.class, () -> reservationService.findOrganizedReservations(2L, 1L));
        verify(userRepository, never()).findById(any());

    }

    @Test
    @DisplayName("Teste aos id's de reservas que o utilizador participará")
    void testIdsOfReservationsToAttend() throws Exception {

        assertThrows(UserPermissionException.class, () -> reservationService.findParticipatingReservations(2L, 1L));
        verify(userRepository, never()).findById(any());

    }

    @Test
    @DisplayName("Utilizador vê as suas próprias reservas organizadas")
    void ownOrganizedReservations() throws Exception {

        User user = new User("Simão", "Teste321%", "lisbon@gmail.com", "123456789", "Lisboa");
        Pitch pitch = new Pitch("Campo das Cebolas", "Lisboa", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
        Reservation reservation = new Reservation(user, pitch, Instant.now(), LocalDateTime.parse("2026-11-10T20:00:00"), LocalDateTime.parse("2026-11-10T21:00:00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.findByOrganizer(user)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.findOrganizedReservations(1L, 1L);

        assertEquals(List.of(reservation), reservations);

    }

    @Test
    @DisplayName("Utilizador vê as reservas em que participa")
    void seeParticipatingReservations() throws Exception {

        User u1 = new User("Simão", "Teste321%", "lisbon@gmail.com", "123456789", "Lisboa");
        User u2 = new User("Leonor", "Teste123&", "leonor@gmail.com", "912345678", "Mem-Martins");
        Pitch pitch = new Pitch("Campo das Cebolas", "Lisboa", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
        Reservation reservation = new Reservation(u1, pitch, Instant.now(), LocalDateTime.parse("2026-11-10T20:00:00"), LocalDateTime.parse("2026-11-10T21:00:00"));
        Invitation invitation = new Invitation(u2, reservation, u1);

        when(userRepository.findById(2L)).thenReturn(Optional.of(u2));
        when(invitationRepository.findByGuestAndStatus(u2, InvitationStatus.ACCEPTED)).thenReturn(List.of(invitation));

        List<Reservation> reservations = reservationService.findParticipatingReservations(2L, 2L);

        assertEquals(List.of(reservation), reservations);

    }

    @Test
    @DisplayName("Teste a cancelamento de reserva sem ser organizador")
    void cancelReservationNotOrganizer() throws Exception {

        User u1 = new User("Simão", "Teste321%", "lisbon@gmail.com", "123456789", "Lisboa");
        u1.setId(1L);
        Pitch pitch = new Pitch("Campo das Cebolas", "Lisboa", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
        Reservation reservation = new Reservation(u1, pitch, Instant.now(), LocalDateTime.parse("2026-11-10T20:00:00"), LocalDateTime.parse("2026-11-10T21:00:00"));

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        assertThrows(UserPermissionException.class, () -> reservationService.cancelReservation(2L, 10L));
        verify(reservationRepository, never()).delete(any());

    }

    @Test
    @DisplayName("Organizador cancela a reserva e os convites são apagados primeiro")
    void cancelReservationAsOrganizer() {

        User u1 = new User("Simão", "Teste321%", "lisbon@gmail.com", "123456789", "Lisboa");
        u1.setId(1L);
        User u2 = new User("Leonor", "Teste123&", "leonor@gmail.com", "912345678", "Mem-Martins");
        u2.setId(2L);
        Pitch pitch = new Pitch("Campo das Cebolas", "Lisboa", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
        Reservation reservation = new Reservation(u1, pitch, Instant.now(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1));
        Invitation invitation = new Invitation(u2, reservation, u1);
        reservation.getInviteList().add(invitation);

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L, 10L);

        InOrder inOrder = inOrder(invitationRepository, reservationRepository);
        inOrder.verify(invitationRepository).deleteAll(reservation.getInviteList());
        inOrder.verify(reservationRepository).delete(reservation);

    }

    @Test
    @DisplayName("Cancelar reserva inexistente dá erro e não apaga nada")
    void cancelReservationNotFound() {

        when(reservationRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.cancelReservation(1L, 10L));

        verify(reservationRepository, never()).delete(any());
        verify(invitationRepository, never()).deleteAll(any());

    }

    @Test
    @DisplayName("Cancelar reserva que já começou dá erro e não apaga nada")
    void cancelReservationAlreadyStarted() {

        User u1 = new User("Simão", "Teste321%", "lisbon@gmail.com", "123456789", "Lisboa");
        u1.setId(1L);
        Pitch pitch = new Pitch("Campo das Cebolas", "Lisboa", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
        Reservation reservation = new Reservation(u1, pitch, Instant.now(), LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        assertThrows(InvalidTimeException.class, () -> reservationService.cancelReservation(1L, 10L));

        verify(reservationRepository, never()).delete(any());
        verify(invitationRepository, never()).deleteAll(any());

    }

}
