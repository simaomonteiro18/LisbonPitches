package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.entities.Invitation;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.Reservation;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.exceptions.InvalidGuestException;
import com.simaomonteiro18.pitchbooking.requests.CreateInvitationRequest;
import com.simaomonteiro18.pitchbooking.services.InvitationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;

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

    User organizer = new User("Simão", "sm@gmail.com", "912345678", "Sintra");
    User guest = new User("Mafalda", "mf@gmail.com", "987654321", "Lisboa");

    Pitch pitch = new Pitch("Jamor", "Oeiras", 20.0, PitchType.ELEVEN);

    Reservation reservation = new Reservation(organizer, pitch, Instant.now(), LocalDateTime.parse("2026-09-28T16:00:00"), LocalDateTime.parse("2026-09-28T18:00:00"));

    @Test
    @DisplayName("Teste a createInvitation() com sucesso")
    public void createInvitationWithSuccess() throws Exception {

        organizer.setId(1L);
        guest.setId(2L);

        reservation.setId(1L);

        CreateInvitationRequest createInvitationRequest = new CreateInvitationRequest(2L, 1L);

        Invitation invitation = new Invitation(guest, reservation);

        when(invitationService.createInvitation(2L, 1L)).thenReturn(invitation);

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

        CreateInvitationRequest createInvitationRequest = new CreateInvitationRequest(1L, 1L);

        Invitation invitation = new Invitation(organizer, reservation);

        when(invitationService.createInvitation(1L, 1L)).thenThrow(InvalidGuestException.class);

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

        Invitation invitation = new Invitation(guest, reservation);
        invitation.setId(7L);

        when(invitationService.acceptInvitation(7L)).thenReturn(invitation);

        mockMvc.perform(patch("/invitations/{id}/accept", 7L))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Teste a rejectInvitation() com sucesso")
    public void rejectInvitationWithSuccess() throws Exception {

        organizer.setId(1L);
        guest.setId(2L);

        reservation.setId(1L);

        Invitation invitation = new Invitation(guest, reservation);
        invitation.setId(7L);

        when(invitationService.rejectInvitation(7L)).thenReturn(invitation);

        mockMvc.perform(patch("/invitations/{id}/reject", 7L))
                .andExpect(status().isOk());

    }

}
