package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.MyReservationsDTO;
import com.simaomonteiro18.lisbonpitches.dtos.ReservationDTO;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.mappers.ReservationMapper;
import com.simaomonteiro18.lisbonpitches.requests.CreateReservationRequest;
import com.simaomonteiro18.lisbonpitches.services.ReservationService;
import com.simaomonteiro18.lisbonpitches.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationRequest request) {

        Long userId = AuthUtils.getAuthenticatedUserId();

        Reservation reservation = reservationService.createReservation(userId, request.pitchId(), request.startTime(), request.endTime());

        ReservationDTO reservationDTO = ReservationMapper.toDTO(reservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDTO);

    }

    @GetMapping(params = "userId")
    public ResponseEntity<MyReservationsDTO> reservationsByUser(@RequestParam("userId") Long userId) {

        List<ReservationDTO> organized = reservationService.findOrganizedReservations(userId).stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());

        List<ReservationDTO> participating = reservationService.findParticipatingReservations(userId).stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(new MyReservationsDTO(organized, participating));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> findById(@PathVariable Long id) {

        Long callerId = AuthUtils.getAuthenticatedUserId();

        Reservation reservation = reservationService.findById(callerId, id);

        ReservationDTO reservationDTO = ReservationMapper.toDTO(reservation);

        return ResponseEntity.ok().body(reservationDTO);

    }

}
