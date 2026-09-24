package com.simaomonteiro18.pitchbooking.dtos;

import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;

import java.math.BigDecimal;

public class PitchDetailDTO {

    private Long id;
    private String name;
    private String city;
    private BigDecimal pricePerHour;
    private PitchType type;
    private PitchAccess pitchAccess;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;

    public PitchDetailDTO() {
    }

    public PitchDetailDTO(Long id, String name, String city, BigDecimal pricePerHour, PitchType type, PitchAccess pitchAccess, String address, Double latitude, Double longitude, String imageUrl) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.type = type;
        this.pitchAccess = pitchAccess;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
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

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
