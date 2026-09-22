package com.simaomonteiro18.pitchbooking.services;

import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.InvitationStatus;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.exceptions.*;
import com.simaomonteiro18.pitchbooking.repositories.InvitationRepository;
import com.simaomonteiro18.pitchbooking.repositories.PitchRepository;
import com.simaomonteiro18.pitchbooking.repositories.ReservationRepository;
import com.simaomonteiro18.pitchbooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PitchRepository pitchRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    public Reservation createReservation(Long userId, Long pitchId, LocalDateTime startTime, LocalDateTime endTime) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new ResourceNotFoundException(Pitch.class, pitchId));

        if (pitch.getPitchAccess() == PitchAccess.PUBLIC) {
            throw new PitchNotBookableException("O Campo é público");
        }

        if (startTime.isAfter(endTime)) {
            throw new InvalidTimeException("Hora de início é posterior à de final! Verifique os campos.");
        }

        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();

        if (minutes < 60L) {
            throw new InvalidTimeException("Tempo inferior a 1 hora!");
        } else if (minutes % 60 != 0) {
            throw new InvalidTimeException("Tempo inválido!");
        }

        boolean exists = reservationRepository.existsOverlappingReservation(pitch, startTime, endTime);

        if (exists) {
            throw new ReservationConflictException("Já existe uma reserva nesse horário.");
        }

        Reservation reservation = new Reservation(user, pitch, Instant.now(), startTime, endTime);

        return reservationRepository.save(reservation);

    }

    public List<Reservation> findOrganizedReservations(Long userId) {

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        return reservationRepository.findByOrganizer(user);

    }

    public List<Reservation> findParticipatingReservations(Long userId) {

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        return invitationRepository.findByGuestAndStatus(user, InvitationStatus.ACCEPTED)
                .stream()
                .map(Invitation::getReservation)
                .collect(Collectors.toList());

    }

    public Reservation findById(Long callerId, Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(Reservation.class, reservationId));

        boolean isOrganizer = reservation.getOrganizer().getId().equals(callerId);
        boolean isGuest = invitationRepository.existsByGuest_IdAndReservation(callerId, reservation);

        if (!isOrganizer && !isGuest) {
            throw new UserPermissionException("Não tem permissões para realizar esta ação.");
        }

        return reservation;

    }

}
