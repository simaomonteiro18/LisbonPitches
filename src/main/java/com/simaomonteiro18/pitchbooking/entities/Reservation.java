package com.simaomonteiro18.pitchbooking.entities;

import com.simaomonteiro18.pitchbooking.entities.enums.InvitationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User organizer;

    @ManyToOne
    private Pitch pitch;


    private Instant moment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    @OneToMany(mappedBy = "reservation")
    private List<Invitation> inviteList = new ArrayList<>();

    public Reservation() {
    }

    public Reservation(User organizer, Pitch pitch, Instant moment, LocalDateTime startTime, LocalDateTime endTime) {
        this.organizer = organizer;
        this.pitch = pitch;
        this.moment = moment;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User user) {
        this.organizer = organizer;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Invitation> getInviteList() {
        return inviteList;
    }

    public int invitesAccepted() {
        long accepted = inviteList.stream()
                .filter(i -> i.getStatus() == InvitationStatus.ACCEPTED)
                .count();
        return (int) accepted;
    }

    public BigDecimal pricePerPerson() {

        Duration duration = Duration.between(startTime, endTime);

        long minutes = duration.toMinutes();

        BigDecimal finalPrice = (pitch.getPricePerHour().multiply(BigDecimal.valueOf(minutes))).divide((BigDecimal.valueOf(60).multiply(BigDecimal.valueOf(invitesAccepted() + 1))), 2, RoundingMode.HALF_UP);

        return finalPrice;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
