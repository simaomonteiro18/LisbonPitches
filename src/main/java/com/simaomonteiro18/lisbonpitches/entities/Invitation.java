package com.simaomonteiro18.lisbonpitches.entities;

import com.simaomonteiro18.lisbonpitches.entities.enums.InvitationStatus;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User guest;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;

    public Invitation() {
    }

    public Invitation(User guest, Reservation reservation, User invitedBy) {
        this.guest = guest;
        this.reservation = reservation;
        this.invitedBy = invitedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getInvitedBy() {
        return invitedBy;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void reject() {
        this.status = InvitationStatus.REJECTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
