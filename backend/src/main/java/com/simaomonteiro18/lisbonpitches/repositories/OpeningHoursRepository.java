package com.simaomonteiro18.lisbonpitches.repositories;

import com.simaomonteiro18.lisbonpitches.entities.OpeningHours;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;
import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {

    Optional<OpeningHours> findByPitchAndDayType(Pitch pitch, DayType dayType);

}
