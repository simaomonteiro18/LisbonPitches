package com.simaomonteiro18.lisbonpitches.dtos;

import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;

import java.math.BigDecimal;

public class PitchSummaryDTO {

    private Long id;
    private String name;
    private String city;
    private BigDecimal pricePerHour;
    private PitchType type;
    private PitchAccess pitchAccess;
    private boolean reservable;

    public PitchSummaryDTO() {
    }

    public PitchSummaryDTO(Long id, String name, String city, BigDecimal pricePerHour, PitchType type, PitchAccess pitchAccess, boolean reservable) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.type = type;
        this.pitchAccess = pitchAccess;
        this.reservable = reservable;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public PitchType getType() {
        return type;
    }

    public PitchAccess getPitchAccess() {
        return pitchAccess;
    }

    public boolean isReservable() {
        return reservable;
    }

}
