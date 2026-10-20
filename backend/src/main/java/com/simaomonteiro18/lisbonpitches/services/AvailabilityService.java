package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AvailabilityService {

    public Set<Integer> occupiedHours(List<Reservation> reservations, LocalDate date) {
        Set<Integer> occupied = new HashSet<>();

        for (Reservation r : reservations) {
            int startHour = r.getStartTime().getHour();
            int endHour = r.getEndTime().toLocalDate().isAfter(date) ? 24 : r.getEndTime().getHour();
            for (int h = startHour; h < endHour; h++) {
                occupied.add(h);
            }
        }

        return occupied;
    }
}
