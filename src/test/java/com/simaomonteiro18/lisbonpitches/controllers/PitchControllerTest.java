package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.PitchDetailDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import com.simaomonteiro18.lisbonpitches.mappers.PitchMapper;
import com.simaomonteiro18.lisbonpitches.services.JwtService;
import com.simaomonteiro18.lisbonpitches.services.PitchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.eq;
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

    Pitch pitch1 = new Pitch("Sintrense", "Sintra", BigDecimal.valueOf(20.0), PitchAccess.PRIVATE, PitchType.ELEVEN);
    Pitch pitch2 = new Pitch("Real", "Massamá", BigDecimal.valueOf(15.0), PitchAccess.PRIVATE, PitchType.ELEVEN);

    @Test
    @DisplayName("Teste a findAll() pitches com sucesso")
    public void findAllPitchesSuccessTest() throws Exception {

        pitches.add(pitch1);
        pitches.add(pitch2);

        when(pitchService.search(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(pitches));
        mockMvc.perform(get("/pitches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1));

    }

    @Test
    @DisplayName("Teste a findByCity() com sucesso")
    public void findByCitySuccessTest() throws Exception {

        pitches.add(pitch1);

        when(pitchService.search(eq("Sintra"), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(pitches));
        mockMvc.perform(get("/pitches").param("city", "Sintra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

    }

    @Test
    @DisplayName("Teste a findFeatured() com sucesso")
    public void findFeaturedSuccessTest() throws Exception {

        List<Pitch> featuredPitches = new ArrayList<>();

        featuredPitches.add(pitch1);
        featuredPitches.add(pitch2);

        List<PitchDetailDTO> pitchDetailDTOS = featuredPitches.stream()
                .map(PitchMapper::toDetailDTO)
                .collect(toList());


        when(pitchService.findFeatured()).thenReturn(pitchDetailDTOS);

        mockMvc.perform(get("/pitches/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(pitchDetailDTOS.size()));

    }

    @Test
    @DisplayName("Teste a findFeatured() vazio")
    public void findFeaturedEmpty() throws Exception {

        when(pitchService.findFeatured()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/pitches/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    @DisplayName("Teste a findAllForMap() com sucesso")
    public void findAllForMapSuccessTest() throws Exception {

        List<Pitch> mapPitches = new ArrayList<>();

        mapPitches.add(pitch1);
        mapPitches.add(pitch2);

        List<PitchDetailDTO> pitchDetailDTOS = mapPitches.stream()
                .map(PitchMapper::toDetailDTO)
                .collect(toList());


        when(pitchService.findAllForMap()).thenReturn(pitchDetailDTOS);

        mockMvc.perform(get("/pitches/map"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(pitchDetailDTOS.size()));

    }

    @Test
    @DisplayName("Teste a findAllForMap() vazio")
    public void findAllForMapEmpty() throws Exception {

        when(pitchService.findAllForMap()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/pitches/map"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }

}
