package com.simaomonteiro18.lisbonpitches.dtos;

import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;

import java.util.List;

public class AvailabilityDTO {

    private AvailabilityStatus status;
    private List<Integer> freeHours;

    public AvailabilityDTO(AvailabilityStatus status, List<Integer> freeHours) {
        this.status = status;
        this.freeHours = freeHours;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public List<Integer> getFreeHours() {
        return freeHours;
    }

}
