package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.dtos.PitchDetailDTO;
import com.simaomonteiro18.pitchbooking.dtos.PitchSummaryDTO;
import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.mappers.PitchMapper;
import com.simaomonteiro18.pitchbooking.services.PitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/pitches")
public class PitchController {

    @Autowired
    private PitchService pitchService;

    @GetMapping
    public ResponseEntity<List<PitchSummaryDTO>> search(@RequestParam(required = false) String city, @RequestParam(required = false) String name) {

        List<Pitch> list = pitchService.search(city, name);

        List<PitchSummaryDTO> finalListPitches = list.stream()
                .map(PitchMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(finalListPitches);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PitchDetailDTO> findById(@PathVariable Long id) {

        Pitch pitch = pitchService.findById(id);

        PitchDetailDTO pitchDetailDTO = PitchMapper.toDetailDTO(pitch);

        return ResponseEntity.ok().body(pitchDetailDTO);

    }

    @GetMapping("/featured")
    public ResponseEntity<List<PitchDetailDTO>> findByFeaturedTrue() {

        List<PitchDetailDTO> pitches = pitchService.findFeatured();

        return ResponseEntity.ok().body(pitches);

    }

    @GetMapping("/map")
    public ResponseEntity<List<PitchDetailDTO>> findAllForMap() {

        List<PitchDetailDTO> pitches = pitchService.findAllForMap();

        return ResponseEntity.ok().body(pitches);

    }

}
