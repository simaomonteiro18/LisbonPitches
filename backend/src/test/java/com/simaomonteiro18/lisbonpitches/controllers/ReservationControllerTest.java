package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.Reservation;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.exceptions.InvalidTimeException;
import com.simaomonteiro18.lisbonpitches.exceptions.UserPermissionException;
import com.simaomonteiro18.lisbonpitches.requests.CreateReservationRequest;
import com.simaomonteiro18.lisbonpitches.services.JwtService;
import com.simaomonteiro18.lisbonpitches.services.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private JwtService jwtService;

    CreateReservationRequest request = new CreateReservationRequest(1L, LocalDateTime.parse("2026-09-28T16:00:00"), LocalDateTime.parse("2026-09-28T18:00:00"));
    CreateReservationRequest request1 = new CreateReservationRequest(1L, LocalDateTime.parse("2026-09-28T18:00:00"), LocalDateTime.parse("2026-09-28T17:00:00"));

    User organizer = new User("Simão", "12345", "sm@gmail.com", "912345678", "Sintra");
    Pitch pitch = new Pitch("Sintrense", "Sintra", BigDecimal.valueOf(20.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
    Reservation reservation = new Reservation(organizer, pitch, Instant.now(), request.startTime(), request.endTime());

    @Test
    @DisplayName("Teste a createReservation() com sucesso")
    public void testCreateReservationWithSuccess() throws Exception {

        when(reservationService.createReservation(1L, 1L, request.startTime(), request.endTime())).thenReturn(reservation);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());

    }

    @Test
    @DisplayName("Teste a createReservation() sem sucesso")
    public void testCreateReservationWithoutSuccess() throws Exception {

        when(reservationService.createReservation(1L, 1L, request1.startTime(), request1.endTime())).thenThrow(InvalidTimeException.class);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                        .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Teste a reservationsByUser() com os dados do próprio utilizador")
    public void testReservationsByUserOwnData() throws Exception {

        authenticateAs(1L);

        when(reservationService.findOrganizedReservations(1L, 1L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations").param("userId", "1")).andExpect(status().isOk());

        verify(reservationService).findOrganizedReservations(1L, 1L);

    }

    @Test
    @DisplayName("Teste a reservationsByUser() com o userId de outro utilizador")
    public void testReservationsByUserOfAnotherUserIsForbidden() throws Exception {

        authenticateAs(2L);

        when(reservationService.findOrganizedReservations(2L, 1L)).thenThrow(UserPermissionException.class);

        mockMvc.perform(get("/reservations?userId=1")).andExpect(status().isForbidden());

    }

    @AfterEach
    public void cleanAfterTests() {

        SecurityContextHolder.clearContext();

    }

    private void authenticateAs(Long userId) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }

}
