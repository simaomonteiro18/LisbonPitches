package com.simaomonteiro18.lisbonpitches.entities;

import com.simaomonteiro18.lisbonpitches.entities.enums.PitchAccess;
import com.simaomonteiro18.lisbonpitches.entities.enums.PitchType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PitchTest {

    @Test
    public void guaranteeDefaultValueOfSlotMinutesIs60() {

        Pitch pitch = new Pitch("InFoot", "Sintra", BigDecimal.valueOf(50.0), PitchAccess.PRIVATE, PitchType.FIVE);

        assertEquals(60, pitch.getSlotMinutes());

    }

}
