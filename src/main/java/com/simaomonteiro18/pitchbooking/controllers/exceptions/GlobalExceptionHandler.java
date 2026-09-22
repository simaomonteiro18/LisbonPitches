package com.simaomonteiro18.pitchbooking.controllers.exceptions;

import com.simaomonteiro18.pitchbooking.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {

        String error = "Resource not found.";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(InvalidTimeException.class)
    public ResponseEntity<StandardError> invalidTime(InvalidTimeException e, HttpServletRequest request) {

        String error = "Invalid time.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<StandardError> reservationConflict(ReservationConflictException e, HttpServletRequest request) {

        String error = "Reservation conflict.";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(InvitationConflictException.class)
    public ResponseEntity<StandardError> invitationConflict(InvitationConflictException e, HttpServletRequest request) {

        String error = "Invitation conflict";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(InvalidGuestException.class)
    public ResponseEntity<StandardError> invalidGuest(InvalidGuestException e, HttpServletRequest request) {
        
        String error = "Invalid guest.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(PitchNotBookableException.class)
    public ResponseEntity<StandardError> pitchNotBookable(PitchNotBookableException e, HttpServletRequest request) {

        String error = "Pitch not Bookable.";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<StandardError> invalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {

        String error = "Invalid Credentials.";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
        
    }

    @ExceptionHandler(EmailConflictException.class)
    public ResponseEntity<StandardError> emailConflict(EmailConflictException e, HttpServletRequest request) {

        String error = "Duplicate Email.";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(UserPermissionException.class)
    public ResponseEntity<StandardError> userPermission(UserPermissionException e, HttpServletRequest request) {

        String error = "User without permission.";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }
    
    @ExceptionHandler(UsernameConflictException.class)
    public ResponseEntity<StandardError> usernameConflict(UsernameConflictException e, HttpServletRequest request) {

        String error = "Duplicate Username.";
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);

    }

}
