package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.lisbonpitches.repositories.OpeningHoursRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AvailabilityService {

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private OpeningHoursRepository openingHoursRepository;

    @Autowired
    private ReservationRepository reservationRepository;

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

    public AvailabilityDTO getAvailabilityOfPitch(Long pitchId, LocalDate date) {

        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new ResourceNotFoundException(Pitch.class, pitchId));

        if (!(pitch.isReservable() && pitch.getPitchAccess() == PitchAccess.PRIVATE)) {
            return new AvailabilityDTO(AvailabilityStatus.NOT_BOOKABLE, List.of());
        }

        return null;

    }

}
