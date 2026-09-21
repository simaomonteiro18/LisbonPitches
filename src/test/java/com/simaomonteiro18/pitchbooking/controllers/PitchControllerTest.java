package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchType;
import com.simaomonteiro18.pitchbooking.services.JwtService;
import com.simaomonteiro18.pitchbooking.services.PitchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PitchController.class)
public class PitchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PitchService pitchService;

    @MockitoBean
    private JwtService jwtService;

    List<Pitch> pitches = new ArrayList<>();

    Pitch pitch1 = new Pitch("Sintrense", "Sintra", 20.0, PitchAccess.PRIVATE, PitchType.ELEVEN);
    Pitch pitch2 = new Pitch("Real", "Massamá", 15.0, PitchAccess.PRIVATE, PitchType.ELEVEN);

    @Test
    @DisplayName("Teste a findAll() pitches com sucesso")
    public void findAllPitchesSuccessTest() throws Exception {

        pitches.add(pitch1);
        pitches.add(pitch2);

        when(pitchService.search(null, null)).thenReturn(pitches);
        mockMvc.perform(get("/pitches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    @DisplayName("Teste a findByCity() com sucesso")
    public void findByCitySuccessTest() throws Exception {

        pitches.add(pitch1);

        when(pitchService.search("Sintra", null)).thenReturn(pitches);
        mockMvc.perform(get("/pitches").param("city", "Sintra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

}
