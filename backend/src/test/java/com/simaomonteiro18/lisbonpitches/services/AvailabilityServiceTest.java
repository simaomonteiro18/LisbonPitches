package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.entities.OpeningHours;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.lisbonpitches.repositories.OpeningHoursRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceTest {

    @Mock
    PitchRepository pitchRepository;

    @Mock
    OpeningHoursRepository openingHoursRepository;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    OpeningHoursService openingHoursService;

    @InjectMocks
    AvailabilityService availabilityService = new AvailabilityService();

    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");

    private Clock clockAt(LocalDateTime dateTime) {
        return Clock.fixed(dateTime.atZone(LISBON).toInstant(), LISBON);
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(availabilityService, "clock",
                clockAt(LocalDateTime.of(2026, 10, 20, 12, 0)));
    }

    @Test
    void notBookableWhenPitchIsNotReservable() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(false);
        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 10, 24));

        assertEquals(AvailabilityStatus.NOT_BOOKABLE, result.getStatus());
        assertEquals(List.of(), result.getSlots());
        assertNull(result.getSlotMinutes());
    }

    @Test
    void pitchNotExistent() {

        when(pitchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 10, 24)));

    }

    @Test
    void testWhenPitchIsPublicAndReservable() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PUBLIC);
        pitch.setReservable(true);
        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 10, 24));

        assertEquals(AvailabilityStatus.NOT_BOOKABLE, result.getStatus());
    }

    @Test
    void noScheduleWhenThereAreNoOpeningHours() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);
        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(LocalDate.of(2026, 10, 24))).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND)).thenReturn(Optional.empty());

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 10, 24));

        assertEquals(AvailabilityStatus.NO_SCHEDULE, result.getStatus());
        assertEquals(List.of(), result.getSlots());
        assertNull(result.getSlotMinutes());
    }

    @Test
    void noSlotsWhenDateIsInThePast() {

        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 19);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKDAY);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKDAY))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(AvailabilityStatus.BOOKABLE, result.getStatus());
        assertEquals(List.of(), result.getSlots());
        
    }

    @Test
    void firstSlotStartsAtOpeningTime() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(date.atTime(18, 0), result.getSlots().get(0).getStart());
    }

    @Test
    void lastSlotStartsAtTwentyThreeWhenPitchClosesAtMidnight() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(0, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(6, result.getSlots().size());
        assertEquals(date.atTime(23, 0), result.getSlots().get(result.getSlots().size() - 1).getStart());
    }

    @Test
    void occupiedSlotIsNotFree() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        Reservation r = new Reservation(null, null, null,
                LocalDateTime.of(2026, 10, 24, 19, 0),
                LocalDateTime.of(2026, 10, 24, 20, 0));

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(reservationRepository.findOverlappingReservations(
                pitch, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(r));
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(4, result.getSlots().size());
        assertTrue(result.getSlots().get(0).isFree());
        assertFalse(result.getSlots().get(1).isFree());
        assertTrue(result.getSlots().get(2).isFree());
        assertTrue(result.getSlots().get(3).isFree());

    }

    @Test
    void pastSlotsAreNotListedWhenDateIsToday() {

        ReflectionTestUtils.setField(availabilityService, "clock", clockAt(LocalDateTime.of(2026, 10, 24, 20, 30)));

        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(1, result.getSlots().size());
        assertEquals(date.atTime(21, 0), result.getSlots().get(0).getStart());

    }

    @Test
    void slotStartingExactlyNowIsNotListed() {

        ReflectionTestUtils.setField(availabilityService, "clock", clockAt(LocalDateTime.of(2026, 10, 24, 21, 0)));

        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(0, result.getSlots().size());

    }

    @Test
    void slotsFollowTheGridOfThirtyMinutes() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);
        pitch.setSlotMinutes(30);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(8, result.getSlots().size());
        assertEquals(date.atTime(18, 30), result.getSlots().get(1).getStart());
        assertEquals(date.atTime(21, 30), result.getSlots().get(7).getStart());
    }

    @Test
    void availabilityReturnsSlotMinutesOfThePitch() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);
        pitch.setSlotMinutes(30);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(18, 0));
        openingHours.setCloseTime(LocalTime.of(22, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(30, result.getSlotMinutes());
    }

    @Test
    void occupiedSlotAfterMidnightIsNotFree() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(true);
        pitch.setSlotMinutes(60);

        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenTime(LocalTime.of(10, 0));
        openingHours.setCloseTime(LocalTime.of(1, 0));

        LocalDate date = LocalDate.of(2026, 10, 24);

        Reservation r = new Reservation(null, null, null,
                LocalDateTime.of(2026, 10, 25, 0, 0),
                LocalDateTime.of(2026, 10, 25, 1, 0));

        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));
        when(openingHoursService.defineDayType(date)).thenReturn(DayType.WEEKEND);
        when(openingHoursRepository.findByPitchAndDayType(pitch, DayType.WEEKEND))
                .thenReturn(Optional.of(openingHours));
        when(reservationRepository.findOverlappingReservations(
                pitch, date.atStartOfDay(), date.plusDays(1).atTime(1, 0)))
                .thenReturn(List.of(r));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, date);

        assertEquals(15, result.getSlots().size());
        assertEquals(date.plusDays(1).atStartOfDay(), result.getSlots().get(14).getStart());
        assertFalse(result.getSlots().get(14).isFree());

    }

}
