package com.simaomonteiro18.pitchbooking.repositories;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PitchRepository extends JpaRepository<Pitch, Long> {

    List<Pitch> findByCity(String city);

    List<Pitch> findByCityAndPitchAccess(String city, PitchAccess pitchAccess);

    List<Pitch> findByPitchAccess(PitchAccess pitchAccess);

    List<Pitch> findByNameContainingIgnoreCaseAndPitchAccess(String name, PitchAccess pitchAccess);

    List<Pitch> findByCityAndNameContainingIgnoreCaseAndPitchAccess(String city, String name, PitchAccess pitchAccess);

    List<Pitch> findByFeaturedTrueAndPitchAccess(PitchAccess pitchAccess);

    boolean existsByName(String name);

}
