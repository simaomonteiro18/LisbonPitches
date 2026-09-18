package com.simaomonteiro18.pitchbooking.services;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.exceptions.InvalidTimeException;
import com.simaomonteiro18.pitchbooking.exceptions.PitchNotBookableException;
import com.simaomonteiro18.pitchbooking.exceptions.ReservationConflictException;
import com.simaomonteiro18.pitchbooking.exceptions.ResourceNotFoundException;
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

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PitchRepository pitchRepository;

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

    public List<Reservation> findReservationsByUser(Long userId) {

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        return reservationRepository.findByOrganizer(user);

    }

}
