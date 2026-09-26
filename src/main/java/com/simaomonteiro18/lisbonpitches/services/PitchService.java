package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.dtos.PitchDetailDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.lisbonpitches.mappers.PitchMapper;
import com.simaomonteiro18.lisbonpitches.repositories.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PitchService {


    @Autowired
    private PitchRepository pitchRepository;

    public List<Pitch> search(String city, String name, PitchAccess pitchAccess) {
        return pitchRepository.search(city, name, pitchAccess);
    }

    public Pitch findById(Long id) {
        return pitchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Pitch.class, id));
    }

    public List<PitchDetailDTO> findFeatured() {

        List<Pitch> pitches = pitchRepository.findByFeaturedTrueAndPitchAccess(PitchAccess.PUBLIC);

        List<PitchDetailDTO> pitchDetailDTOS = pitches.stream()
                .map(PitchMapper::toDetailDTO)
                .collect(Collectors.toList());

        return pitchDetailDTOS;
        
    }

    public List<PitchDetailDTO> findAllForMap() {

        List<Pitch> pitches = pitchRepository.findAll();

        List<PitchDetailDTO> pitchDetailDTOS = pitches.stream()
                .map(PitchMapper::toDetailDTO)
                .collect(Collectors.toList());

        return pitchDetailDTOS;

    }

}
