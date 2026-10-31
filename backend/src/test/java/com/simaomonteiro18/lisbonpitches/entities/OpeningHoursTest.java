package com.simaomonteiro18.lisbonpitches.entities;


import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class OpeningHoursTest {

    OpeningHours openingHours = new OpeningHours();

    @Test
    public void guaranteeSetOpenHourFillsOpenTime() {
        openingHours.setOpenHour(10);
        assertEquals(LocalTime.of(10, 0), openingHours.getOpenTime());
    }

    @Test
    public void guaranteeSetOpenHourFillsOpenTimeWithAnotherHour() {
        openingHours.setOpenHour(19);
        assertEquals(LocalTime.of(19, 0), openingHours.getOpenTime());
    }

    @Test
    public void guaranteeSetCloseHourFillsOpenTime() {
        openingHours.setCloseHour(22);
        assertEquals(LocalTime.of(22, 0), openingHours.getCloseTime());
    }

    @Test
    public void guaranteeSetCloseHourToMidnight() {
        openingHours.setCloseHour(24);
        assertEquals(LocalTime.MIDNIGHT, openingHours.getCloseTime());
    }

    @Test
    public void guaranteeSetCloseTimeAfterMidnightKeepsCloseHourAtTwentyFour() {
        openingHours.setOpenHour(10);
        openingHours.setCloseTime(LocalTime.of(1, 0));
        assertEquals(24, openingHours.getCloseHour());
    }

    @Test
    public void guaranteeSetCloseTimeStoresCloseTime() {
        openingHours.setOpenHour(10);
        openingHours.setCloseTime(LocalTime.of(1, 0));
        assertEquals(LocalTime.of(1, 0), openingHours.getCloseTime());
    }

    @Test
    public void guaranteeSetCloseTimeBeforeMidnightSetsCloseHour() {
        openingHours.setOpenHour(10);
        openingHours.setCloseTime(LocalTime.of(22, 0));
        assertEquals(22, openingHours.getCloseHour());
    }

}
