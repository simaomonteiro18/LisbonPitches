package com.simaomonteiro18.pitchbooking.requests;

import java.time.LocalDateTime;

public record CreateReservationRequest(Long pitchId, LocalDateTime startTime, LocalDateTime endTime) {

}
