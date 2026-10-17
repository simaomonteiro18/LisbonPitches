package com.simaomonteiro18.lisbonpitches.repositories;

import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    boolean existsByGuestAndReservation(User guest, Reservation reservation);

    List<Invitation> getAllInvitationsByReservation(Reservation reservation);

    boolean existsByGuest_IdAndReservation(Long guestId, Reservation reservation);

    List<Invitation> findByGuestAndStatus(User guest, InvitationStatus status);

    List<Invitation> findByGuest_Id(Long guestId);

}
