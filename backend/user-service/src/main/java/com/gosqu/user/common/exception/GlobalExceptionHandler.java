package com.gosqu.user.common.exception;

import com.gosqu.user.address.exception.AddressLimitExceededException;
import com.gosqu.user.address.exception.AddressNotFoundException;
import com.gosqu.user.profile.exception.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleProfileNotFound(ProfileNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAddressNotFound(AddressNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AddressLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleAddressLimit(AddressLimitExceededException ex) {
        return ResponseEntity.status(422).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Map.of("error", message);
    }
}
