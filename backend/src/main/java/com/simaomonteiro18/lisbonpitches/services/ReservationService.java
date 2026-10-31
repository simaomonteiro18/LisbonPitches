package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.dtos.SlotDTO;
import com.simaomonteiro18.lisbonpitches.entities.Invitation;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.InvitationStatus;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.exceptions.*;
import com.simaomonteiro18.lisbonpitches.repositories.InvitationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import com.simaomonteiro18.lisbonpitches.repositories.ReservationRepository;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    private AvailabilityService availabilityService;

    @Transactional
    public Reservation createReservation(Long userId, Long pitchId, LocalDateTime startTime, LocalDateTime endTime) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        Pitch pitch = pitchRepository.findWithLockById(pitchId)
                .orElseThrow(() -> new ResourceNotFoundException(Pitch.class, pitchId));
        int slotMinutes = pitch.getSlotMinutes();

        if (pitch.getPitchAccess() == PitchAccess.PUBLIC) {
            throw new PitchNotBookableException("O Campo é público");
        }

        if (startTime.isAfter(endTime)) {
            throw new InvalidTimeException("Hora de início é posterior à de final! Verifique os campos.");
        }

        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();

        if (minutes < slotMinutes) {
            throw new InvalidTimeException("Tempo inferior à duração de um slot!");
        } else if (minutes % slotMinutes != 0) {
            throw new InvalidTimeException("Tempo inválido!");
        }

        AvailabilityDTO availabilityDTO = availabilityService.getAvailabilityOfPitch(pitchId, startTime.toLocalDate());

        if (availabilityDTO.getStatus() == AvailabilityStatus.NOT_BOOKABLE) {
            throw new PitchNotBookableException("O campo não é reservável.");
        }

        if (availabilityDTO.getStatus() == AvailabilityStatus.NO_SCHEDULE) {
            throw new PitchNotBookableException("O campo não tem horário definido para esse dia");
        }

        List<SlotDTO> slots = availabilityDTO.getSlots();

        boolean startFound = false;
        for (SlotDTO s : slots) {
            if (s.getStart().equals(startTime)) {
                startFound = true;
            }
        }

        if (!startFound) {
            AvailabilityDTO previousDay = availabilityService.getAvailabilityOfPitch(pitchId, startTime.toLocalDate().minusDays(1));
            if (previousDay.getStatus() == AvailabilityStatus.BOOKABLE) {
                slots = previousDay.getSlots();
            }
        }

        for (LocalDateTime t = startTime; t.isBefore(endTime); t = t.plusMinutes(slotMinutes)) {
            boolean freeSlot = false;
            for (SlotDTO s : slots) {
                if (s.getStart().equals(t) && s.isFree()) {
                    freeSlot = true;
                }
            }
            if (!freeSlot) {
                throw new InvalidTimeException("A hora não está disponível");
            }
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
