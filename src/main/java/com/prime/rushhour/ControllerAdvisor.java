package com.prime.rushhour;

import com.prime.rushhour.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ActivityNotFoundException.class, UserNotFoundException.class, AppointmentNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex) {
        String bodyOfResponse = ex.getMessage();
        return new ResponseEntity<>(bodyOfResponse,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ThisEmailAlreadyExistsException.class, AppointmentExistsException.class})
    public ResponseEntity<Object> handleThisEmailExists(RuntimeException ex) {
        String bodyResponse = ex.getMessage();
        return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(RuntimeException ex) {
        String bodyResponse = ex.getMessage();
        return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
