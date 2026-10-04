package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.PitchDetailDTO;
import com.simaomonteiro18.lisbonpitches.dtos.PitchSummaryDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.mappers.PitchMapper;
import com.simaomonteiro18.lisbonpitches.services.PitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/pitches")
public class PitchController {

    @Autowired
    private PitchService pitchService;

    @GetMapping
    public PagedModel<PitchSummaryDTO> search(@RequestParam(required = false) String city, @RequestParam(required = false) String name, @RequestParam(required = false) PitchAccess pitchAccess, Pageable pageable) {

        Page<Pitch> list = pitchService.search(city, name, pitchAccess, pageable);

        PagedModel<PitchSummaryDTO> finalListPitches = new PagedModel<PitchSummaryDTO>(list.map(PitchMapper::toDTO));

        return finalListPitches;

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
