package com.simaomonteiro18.lisbonpitches.dtos;

import java.util.List;

public class MyReservationsDTO {

    private List<ReservationDTO> organized;
    private List<ReservationDTO> participating;

    public MyReservationsDTO() {
    }

    public MyReservationsDTO(List<ReservationDTO> organized, List<ReservationDTO> participating) {
        this.organized = organized;
        this.participating = participating;
    }

    public List<ReservationDTO> getOrganized() {
        return organized;
    }

    public List<ReservationDTO> getParticipating() {
        return participating;
    }

}
