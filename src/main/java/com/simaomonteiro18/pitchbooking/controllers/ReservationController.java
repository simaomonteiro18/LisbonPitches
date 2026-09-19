package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.dtos.ReservationDTO;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.mappers.ReservationMapper;
import com.simaomonteiro18.pitchbooking.requests.CreateReservationRequest;
import com.simaomonteiro18.pitchbooking.services.ReservationService;
import com.simaomonteiro18.pitchbooking.utils.AuthUtils;
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
    public ResponseEntity<List<ReservationDTO>> reservationsByUser(@RequestParam("userId") Long userId) {

        List<Reservation> reservations = reservationService.findReservationsByUser(userId);

        List<ReservationDTO> reservationDTOList = reservations.stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(reservationDTOList);

    }

}
