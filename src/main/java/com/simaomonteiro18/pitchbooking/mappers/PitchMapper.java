package com.simaomonteiro18.pitchbooking.mappers;

import com.simaomonteiro18.pitchbooking.dtos.PitchDetailDTO;
import com.simaomonteiro18.pitchbooking.dtos.PitchSummaryDTO;
import com.simaomonteiro18.pitchbooking.entities.Pitch;

public class PitchMapper {
        public static PitchSummaryDTO toDTO(Pitch pitch) {

            PitchSummaryDTO pitchSummaryDTO = new PitchSummaryDTO(pitch.getId(), pitch.getName(), pitch.getCity(), pitch.getPricePerHour(), pitch.getPitchType(), pitch.getPitchAccess());

            return pitchSummaryDTO;

        }

        public static PitchDetailDTO toDetailDTO(Pitch pitch) {

            PitchDetailDTO pitchDetailDTO = new PitchDetailDTO(pitch.getId(), pitch.getName(), pitch.getCity(), pitch.getPricePerHour(), pitch.getPitchType(), pitch.getPitchAccess(), pitch.getAddress(), pitch.getLatitude(), pitch.getLongitude(), pitch.getImageUrl());

            return pitchDetailDTO;

        }
}
