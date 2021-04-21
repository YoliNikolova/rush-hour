package com.prime.rushhour;

import com.prime.rushhour.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ActivityNotFoundException.class, UserNotFoundException.class, AppointmentNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex) {
        String bodyOfResponse = ex.getMessage();
        return new ResponseEntity<>(bodyOfResponse,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ThisEmailAlreadyExistsException.class})
    public ResponseEntity<Object> handleThisEmailExists(RuntimeException ex) {
        String bodyResponse = ex.getMessage();
        return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(RuntimeException ex) {
        String bodyResponse = ex.getMessage();
        return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
    }
}
