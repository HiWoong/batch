package com.ktds.batch.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public void handleResponseStatusException(ResponseStatusException exception) {
        log.error("[ResponseStatusException] status: {} | errorMessage: {}", exception.getStatusCode().value(),
            exception.getReason());
    }

    @ExceptionHandler(Exception.class)
    public void handleAllExceptions(Exception exception) {
        log.error("Unexpected Exception: {}", exception.getMessage());
    }

}
