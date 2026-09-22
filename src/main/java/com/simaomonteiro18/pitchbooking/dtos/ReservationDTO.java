package com.simaomonteiro18.pitchbooking.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class ReservationDTO {

    private Long id;
    private Long organizerId;
    private String organizerName;
    private PitchSummaryDTO pitch;
    private Instant moment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal pricePerPerson;
    private int invitesAccepted;

    public ReservationDTO() {
    }

    public ReservationDTO(Long id, Long organizerId, String organizerName, PitchSummaryDTO pitch, Instant moment, LocalDateTime startTime, LocalDateTime endTime, BigDecimal pricePerPerson, int invitesAccepted) {
        this.id = id;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.pitch = pitch;
        this.moment = moment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pricePerPerson = pricePerPerson;
        this.invitesAccepted = invitesAccepted;
    }

    public Long getId() {
        return id;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public PitchSummaryDTO getPitch() {
        return pitch;
    }

    public Instant getMoment() {
        return moment;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getPricePerPerson() {
        return pricePerPerson;
    }

    public int getInvitesAccepted() {
        return invitesAccepted;
    }

}
