package com.simaomonteiro18.lisbonpitches.dtos;

import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;

import java.math.BigDecimal;

public class PitchDetailDTO {

    private Long id;
    private String name;
    private String city;
    private BigDecimal pricePerHour;
    private PitchType type;
    private PitchAccess pitchAccess;
    private boolean reservable;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;

    public PitchDetailDTO() {
    }

    public PitchDetailDTO(Long id, String name, String city, BigDecimal pricePerHour, PitchType type, PitchAccess pitchAccess, boolean reservable, String contactPhone, String contactEmail, String address, Double latitude, Double longitude, String imageUrl) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.type = type;
        this.pitchAccess = pitchAccess;
        this.reservable = reservable;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
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

    public boolean isReservable() {
        return reservable;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
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
