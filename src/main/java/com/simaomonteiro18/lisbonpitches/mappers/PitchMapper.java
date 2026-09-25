package com.simaomonteiro18.lisbonpitches.mappers;

import com.simaomonteiro18.lisbonpitches.dtos.PitchDetailDTO;
import com.simaomonteiro18.lisbonpitches.dtos.PitchSummaryDTO;
import com.simaomonteiro18.lisbonpitches.entities.Pitch;

public class PitchMapper {
        public static PitchSummaryDTO toDTO(Pitch pitch) {

            PitchSummaryDTO pitchSummaryDTO = new PitchSummaryDTO(pitch.getId(), pitch.getName(), pitch.getCity(), pitch.getPricePerHour(), pitch.getPitchType(), pitch.getPitchAccess(), pitch.isReservable());

            return pitchSummaryDTO;

        }

        public static PitchDetailDTO toDetailDTO(Pitch pitch) {

            PitchDetailDTO pitchDetailDTO = new PitchDetailDTO(pitch.getId(), pitch.getName(), pitch.getCity(), pitch.getPricePerHour(), pitch.getPitchType(), pitch.getPitchAccess(), pitch.isReservable(), pitch.getContactPhone(), pitch.getContactEmail(), pitch.getAddress(), pitch.getLatitude(), pitch.getLongitude(), pitch.getImageUrl());

            return pitchDetailDTO;

        }
}
