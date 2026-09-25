package com.simaomonteiro18.lisbonpitches.repositories;

import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByOrganizer(User organizer);

    @Query("""
    SELECT COUNT(r) > 0
    FROM Reservation r
    WHERE r.pitch = :pitch
      AND r.startTime < :endTime
      AND r.endTime > :startTime
    """)
    boolean existsOverlappingReservation(
            @Param("pitch") Pitch pitch,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

}
