package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OpeningHoursServiceTest {

    OpeningHoursService openingHoursService = new OpeningHoursService();

    @ParameterizedTest
    @CsvSource({
            "2026-10-19, WEEKDAY",
            "2026-10-20, WEEKDAY",
            "2026-10-21, WEEKDAY",
            "2026-10-22, WEEKDAY",
            "2026-10-23, WEEKDAY",
            "2026-10-24, WEEKEND",
            "2026-10-25, WEEKEND",

    })
    public void getDayTypeWithSuccess(LocalDate date, DayType expected) {

        DayType actual = openingHoursService.defineDayType(date);

        assertEquals(expected, actual);

    }

}
