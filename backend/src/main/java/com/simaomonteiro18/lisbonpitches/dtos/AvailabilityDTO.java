package com.simaomonteiro18.lisbonpitches.dtos;

import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;

import java.util.List;

public class AvailabilityDTO {

    private AvailabilityStatus status;
    private List<Integer> freeHours;
    private Integer openHour;
    private Integer closeHour;

    public AvailabilityDTO(AvailabilityStatus status, List<Integer> freeHours) {
        this(status, freeHours, null, null);
    }

    public AvailabilityDTO(AvailabilityStatus status, List<Integer> freeHours, Integer openHour, Integer closeHour) {
        this.status = status;
        this.freeHours = freeHours;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public List<Integer> getFreeHours() {
        return freeHours;
    }

    public Integer getOpenHour() {
        return openHour;
    }

    public Integer getCloseHour() {
        return closeHour;
    }

}
