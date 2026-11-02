package com.simaomonteiro18.lisbonpitches.dtos;

import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;

import java.util.List;

public class AvailabilityDTO {

    private AvailabilityStatus status;
    private List<SlotDTO> slots;
    private Integer slotMinutes;

    public AvailabilityDTO(AvailabilityStatus status) {
        this(status, List.of(), null);
    }

    public AvailabilityDTO(AvailabilityStatus status, List<SlotDTO> slots, Integer slotMinutes) {
        this.status = status;
        this.slots = slots;
        this.slotMinutes = slotMinutes;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public List<SlotDTO> getSlots() {
        return slots;
    }

    public Integer getSlotMinutes() {
        return slotMinutes;
    }

}
