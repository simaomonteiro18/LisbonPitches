package com.simaomonteiro18.lisbonpitches.requests;

import java.time.LocalDateTime;

public record CreateReservationRequest(Long pitchId, LocalDateTime startTime, LocalDateTime endTime) {

}
