package com.simaomonteiro18.pitchbooking.repositories;

import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    boolean existsByGuestAndReservation(User guest, Reservation reservation);

    List<Invitation> getAllInvitationsByReservation(Reservation reservation);

    boolean existsByGuest_IdAndReservation(Long guestId, Reservation reservation);

}
