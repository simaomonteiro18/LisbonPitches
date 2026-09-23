package com.simaomonteiro18.pitchbooking.services;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import com.simaomonteiro18.pitchbooking.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.pitchbooking.repositories.PitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PitchService {


    @Autowired
    private PitchRepository pitchRepository;

    public List<Pitch> findByPitchAccess() {
        return pitchRepository.findByPitchAccess(PitchAccess.PUBLIC);
    }

    public List<Pitch> findByCityAndPitchAccess(String city){
        return pitchRepository.findByCityAndPitchAccess(city, PitchAccess.PUBLIC);
    }

    public List<Pitch> findByName(String name) {
        return pitchRepository.findByNameContainingIgnoreCaseAndPitchAccess(name, PitchAccess.PUBLIC);
    }

    public List<Pitch> search(String city, String name) {
        if (city != null && name != null) {
            return pitchRepository.findByCityAndNameContainingIgnoreCaseAndPitchAccess(city, name, PitchAccess.PUBLIC);
        } else if (city != null) {
            return pitchRepository.findByCityAndPitchAccess(city, PitchAccess.PUBLIC);
        } else if (name != null) {
            return pitchRepository.findByNameContainingIgnoreCaseAndPitchAccess(name, PitchAccess.PUBLIC);
        } else {
            return pitchRepository.findByPitchAccess(PitchAccess.PUBLIC);
        }
    }

    public Pitch findById(Long id) {
        return pitchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Pitch.class, id));
    }

}
