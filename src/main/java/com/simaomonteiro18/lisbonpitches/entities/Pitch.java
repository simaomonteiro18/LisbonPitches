package com.simaomonteiro18.lisbonpitches.entities;

import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "pitches")
public class Pitch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String city;
    private BigDecimal pricePerHour;

    @Enumerated(EnumType.STRING)
    private PitchAccess pitchAccess;

    @Enumerated(EnumType.STRING)
    private PitchType pitchType;

    private boolean featured;
    private boolean reservable;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private Double latitude;
    private Double longitude;
    @Column(length = 1000)
    private String imageUrl;

    public Pitch() {

    }

    public Pitch(String name, String city, BigDecimal pricePerHour, PitchAccess pitchAccess, PitchType pitchType) {
        this.name = name;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.pitchAccess = pitchAccess;
        this.pitchType = pitchType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public PitchAccess getPitchAccess() {
        return pitchAccess;
    }

    public void setPitchAccess(PitchAccess pitchAccess) {
        this.pitchAccess = pitchAccess;
    }

    public PitchType getPitchType() {
        return pitchType;
    }

    public void setPitchType(PitchType pitchType) {
        this.pitchType = pitchType;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isReservable() {
        return reservable;
    }

    public void setReservable(boolean reservable) {
        this.reservable = reservable;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pitch pitch = (Pitch) o;
        return Objects.equals(id, pitch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pitch{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", pitchAccess=" + pitchAccess +
                ", pitchType=" + pitchType +
                '}';
    }
    
}
