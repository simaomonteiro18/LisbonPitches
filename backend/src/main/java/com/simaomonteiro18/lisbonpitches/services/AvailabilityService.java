package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.dtos.SlotDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AvailabilityService {

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private OpeningHoursRepository openingHoursRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OpeningHoursService openingHoursService;

    @Autowired
    private Clock clock;

    public Set<Integer> occupiedHours(List<Reservation> reservations, LocalDate date) {
        Set<Integer> occupied = new HashSet<>();

        for (Reservation r : reservations) {
            if (r.getStartTime().toLocalDate().isAfter(date)) {
                continue;
            }
            int startHour = r.getStartTime().getHour();
            int endHour = r.getEndTime().toLocalDate().isAfter(date) ? 24 : r.getEndTime().getHour();
            for (int h = startHour; h < endHour; h++) {
                occupied.add(h);
            }
        }

        return occupied;
    }

    public AvailabilityDTO getAvailabilityOfPitch(Long pitchId, LocalDate date) {

        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new ResourceNotFoundException(Pitch.class, pitchId));

        if (!(pitch.isReservable() && pitch.getPitchAccess() == PitchAccess.PRIVATE)) {
            return new AvailabilityDTO(AvailabilityStatus.NOT_BOOKABLE, List.of());
        }

        DayType dateType = openingHoursService.defineDayType(date);

        Optional<OpeningHours> hours = openingHoursRepository.findByPitchAndDayType(pitch, dateType);

        if (hours.isEmpty()) {
            return new AvailabilityDTO(AvailabilityStatus.NO_SCHEDULE, List.of());
        }

        OpeningHours openingHours = hours.get();
        int open = openingHours.getOpenHour();
        int close = openingHours.getCloseHour();

        LocalDateTime slotStart = date.atTime(openingHours.getOpenTime());
        LocalDateTime windowEnd = date.atTime(openingHours.getCloseTime());
        if (windowEnd.isBefore(slotStart)) {
            windowEnd = windowEnd.plusDays(1);
        }

        LocalDateTime searchEnd = date.plusDays(1).atStartOfDay();
        if (windowEnd.isAfter(searchEnd)) {
            searchEnd = windowEnd;
        }

        List<Reservation> reservations = reservationRepository.findOverlappingReservations(
                pitch, date.atStartOfDay(), searchEnd);

        Set<Integer> occupied = occupiedHours(reservations, date);

        LocalDateTime now = LocalDateTime.now(clock);
        boolean isToday = date.equals(now.toLocalDate());

        List<Integer> freeHours = new ArrayList<>();

        for (int h = open; h < close; h++) {
            boolean isPastDate = date.isBefore(now.toLocalDate());
            boolean isPast = isPastDate || (isToday && h <= now.getHour());
            if (!occupied.contains(h) && !isPast) {
                freeHours.add(h);
            }
        }

        List<SlotDTO> slots = new ArrayList<>();

        int slotMinutes = pitch.getSlotMinutes();

        while (!slotStart.plusMinutes(slotMinutes).isAfter(windowEnd)) {
            LocalDateTime slotEnd = slotStart.plusMinutes(slotMinutes);

            if (slotStart.isAfter(now)) {
                boolean occupiedSlot = false;

                for (Reservation r : reservations) {
                    if (r.getStartTime().isBefore(slotEnd) && r.getEndTime().isAfter(slotStart)) {
                        occupiedSlot = true;
                    }
                }

                slots.add(new SlotDTO(slotStart, !occupiedSlot));
            }

            slotStart = slotEnd;
        }

        return new AvailabilityDTO(AvailabilityStatus.BOOKABLE, freeHours, openingHours.getOpenHour(), openingHours.getCloseHour(), slots, slotMinutes);

    }

}
