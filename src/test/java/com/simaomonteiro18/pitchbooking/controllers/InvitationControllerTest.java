package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.exceptions.InvalidGuestException;
import com.simaomonteiro18.pitchbooking.requests.CreateInvitationRequest;
import com.simaomonteiro18.pitchbooking.services.InvitationService;
import com.simaomonteiro18.pitchbooking.services.JwtService;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvitationController.class)
public class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InvitationService invitationService;

    @MockitoBean
    private JwtService jwtService;

    User organizer = new User("Simão", "simaomonteiro", "12345", "sm@gmail.com", "912345678", "Sintra");
    User guest = new User("Mafalda", "mafalda", "1234567", "mf@gmail.com", "987654321", "Lisboa");

    Pitch pitch = new Pitch("Jamor", "Oeiras", null, PitchAccess.PUBLIC, PitchType.ELEVEN);

    Reservation reservation = new Reservation(organizer, pitch, Instant.now(), LocalDateTime.parse("2026-09-28T16:00:00"), LocalDateTime.parse("2026-09-28T18:00:00"));

    @Test
    @DisplayName("Teste a createInvitation() com sucesso")
    public void createInvitationWithSuccess() throws Exception {

        organizer.setId(1L);
        guest.setId(2L);

        reservation.setId(1L);

        CreateInvitationRequest createInvitationRequest = new CreateInvitationRequest(1L, guest.getUsername());

        Invitation invitation = new Invitation(guest, reservation, organizer);

        when(invitationService.createInvitation(2L, guest.getUsername(), 1L)).thenReturn(invitation);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(2L, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockMvc.perform(post("/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvitationRequest)))
                        .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("Teste a createInvitation() sem sucesso")
    public void createInvitationWithoutSuccess() throws Exception {

        organizer.setId(1L);

        reservation.setId(1L);

        CreateInvitationRequest createInvitationRequest = new CreateInvitationRequest(1L, guest.getUsername());

        Invitation invitation = new Invitation(guest, reservation, organizer);

        when(invitationService.createInvitation(1L, guest.getUsername(), 1L)).thenThrow(InvalidGuestException.class);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockMvc.perform(post("/invitations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createInvitationRequest)))
                    .andExpect(status().isBadRequest());



    }

    @Test
    @DisplayName("Teste a acceptInvitation() com sucesso")
    public void acceptInvitationWithSuccess() throws Exception {

        organizer.setId(1L);
        guest.setId(2L);

        reservation.setId(1L);

        Invitation invitation = new Invitation(guest, reservation, organizer);
        invitation.setId(7L);

        when(invitationService.acceptInvitation(1L, 7L)).thenReturn(invitation);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockMvc.perform(patch("/invitations/{id}/accept", 7L))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Teste a rejectInvitation() com sucesso")
    public void rejectInvitationWithSuccess() throws Exception {

        organizer.setId(1L);
        guest.setId(2L);

        reservation.setId(1L);

        Invitation invitation = new Invitation(guest, reservation, organizer);
        invitation.setId(7L);

        when(invitationService.rejectInvitation(1L, 7L)).thenReturn(invitation);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockMvc.perform(patch("/invitations/{id}/reject", 7L))
                .andExpect(status().isOk());

    }

    @AfterEach
    public void cleanAfterTests() {
        
        SecurityContextHolder.clearContext();

    }

}
