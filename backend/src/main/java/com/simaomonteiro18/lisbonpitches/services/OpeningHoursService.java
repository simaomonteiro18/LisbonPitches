package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import com.simaomonteiro18.lisbonpitches.repositories.OpeningHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class OpeningHoursService {

    @Autowired
    private OpeningHoursRepository openingHoursRepository;

    public DayType defineDayType(LocalDate date) {

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return DayType.WEEKEND;
        } else {
            return DayType.WEEKDAY;
        }

    }

}
