package com.simaomonteiro18.pitchbooking.mappers;

import com.simaomonteiro18.pitchbooking.dtos.PitchSummaryDTO;
import com.simaomonteiro18.pitchbooking.entities.Pitch;

import java.math.BigDecimal;

public class PitchMapper {
        public static PitchSummaryDTO toDTO(Pitch pitch) {

            PitchSummaryDTO pitchSummaryDTO = new PitchSummaryDTO(pitch.getId(), pitch.getName(), pitch.getCity(), pitch.getPricePerHour(), pitch.getPitchType());

            return pitchSummaryDTO;

        }
}
