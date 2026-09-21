package com.simaomonteiro18.pitchbooking.dtos;

import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;

import java.math.BigDecimal;

public class PitchSummaryDTO {

    private Long id;
    private String name;
    private String city;
    private BigDecimal pricePerHour;
    private PitchType type;

    public PitchSummaryDTO() {
    }

    public PitchSummaryDTO(Long id, String name, String city, BigDecimal pricePerHour, PitchType type) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.type = type;
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

}
