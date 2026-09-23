package com.simaomonteiro18.pitchbooking.mappers;

import com.simaomonteiro18.pitchbooking.dtos.PitchSummaryDTO;
import com.simaomonteiro18.pitchbooking.dtos.ReservationDTO;
import com.simaomonteiro18.pitchbooking.entities.Reservation;

public class ReservationMapper {

    public static ReservationDTO toDTO(Reservation reservation) {

        PitchSummaryDTO pitch = new PitchSummaryDTO(reservation.getPitch().getId(), reservation.getPitch().getName(), reservation.getPitch().getCity(), reservation.getPitch().getPricePerHour(), reservation.getPitch().getPitchType(), reservation.getPitch().getPitchAccess());

        ReservationDTO reservationDTO = new ReservationDTO(reservation.getId(), reservation.getOrganizer().getId(), reservation.getOrganizer().getName(), pitch, reservation.getMoment(), reservation.getStartTime(), reservation.getEndTime(), reservation.pricePerPerson(), reservation.invitesAccepted());

        return reservationDTO;

    }

}
