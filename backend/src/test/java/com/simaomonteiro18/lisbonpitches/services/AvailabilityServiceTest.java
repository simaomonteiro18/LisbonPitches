package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.lisbonpitches.repositories.OpeningHoursRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void occupiedHoursForTwoHourReservation() {
        LocalDate date = LocalDate.of(2026, 10, 24);
        Reservation r = new Reservation(null, null, null,
                LocalDateTime.of(2026, 10, 24, 20, 0),
                LocalDateTime.of(2026, 10, 24, 22, 0));

        Set<Integer> result = availabilityService.occupiedHours(List.of(r), date);

        assertEquals(Set.of(20, 21), result);
    }

    @Test
    void occupiedHoursForReservationEndingAtMidnight() {
        LocalDate date = LocalDate.of(2026, 10, 24);
        Reservation r = new Reservation(null, null, null,
                LocalDateTime.of(2026, 10, 24, 23, 0),
                LocalDateTime.of(2026, 10, 25, 0, 0));

        Set<Integer> result = availabilityService.occupiedHours(List.of(r), date);

        assertEquals(Set.of(23), result);
    }

    @Test
    void occupiedHoursWithNoReservations() {
        LocalDate date = LocalDate.of(2026, 10, 24);

        Set<Integer> result = availabilityService.occupiedHours(List.of(), date);

        assertEquals(Set.of(), result);
    }

    @Test
    void notBookableWhenPitchIsNotReservable() {
        Pitch pitch = new Pitch();
        pitch.setPitchAccess(PitchAccess.PRIVATE);
        pitch.setReservable(false);
        when(pitchRepository.findById(1L)).thenReturn(Optional.of(pitch));

        AvailabilityDTO result = availabilityService.getAvailabilityOfPitch(1L, LocalDate.of(2026, 10, 24));

        assertEquals(AvailabilityStatus.NOT_BOOKABLE, result.getStatus());
        assertEquals(List.of(), result.getFreeHours());
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
        assertEquals(List.of(), result.getFreeHours());
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
        assertEquals(List.of(), result.getFreeHours());
    }

}
