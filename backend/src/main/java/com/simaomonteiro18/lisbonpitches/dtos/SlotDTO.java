package com.simaomonteiro18.lisbonpitches.dtos;

import java.time.LocalDateTime;

public class SlotDTO {

    private LocalDateTime start;
    private boolean free;

    public SlotDTO(LocalDateTime start, boolean free) {
        this.start = start;
        this.free = free;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public boolean isFree() {
        return free;
    }

}
