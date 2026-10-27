package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.AvailabilityDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.enums.AvailabilityStatus;
import com.simaomonteiro18.lisbonpitches.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.lisbonpitches.services.AvailabilityService;
import com.simaomonteiro18.lisbonpitches.services.JwtService;
import com.simaomonteiro18.lisbonpitches.services.PitchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
public class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AvailabilityService availabilityService;

    @Test
    public void gettingAvailabilityFromABookablePitch() throws Exception {

        LocalDate date = LocalDate.of(2026, 10, 24);
        AvailabilityDTO dto = new AvailabilityDTO(AvailabilityStatus.BOOKABLE, List.of(18, 20, 21), 18, 22);

        when(availabilityService.getAvailabilityOfPitch(1L, date)).thenReturn(dto);

        mockMvc.perform(get("/pitches/1/availability").param("date", "2026-10-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKABLE"))
                .andExpect(jsonPath("$.freeHours").value(contains(18, 20, 21)))
                .andExpect(jsonPath("$.openHour").value(18))
                .andExpect(jsonPath("$.closeHour").value(22));

    }

    @Test
    public void gettingAvailabilityFromANonExistentPitch() throws Exception {

        LocalDate date = LocalDate.of(2026, 10, 24);

        when(availabilityService.getAvailabilityOfPitch(1L, date)).thenThrow(new ResourceNotFoundException(Pitch.class, 1L));

        mockMvc.perform(get("/pitches/1/availability").param("date", "2026-10-24"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void gettingAvailabilityWithoutDateReturnsBadRequest() throws Exception {

        mockMvc.perform(get("/pitches/1/availability"))
                .andExpect(status().isBadRequest());

    }

}

