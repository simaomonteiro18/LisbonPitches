package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvailabilityServiceTest {

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

}
