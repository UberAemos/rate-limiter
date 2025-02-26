package com.uberaemos.ratelimiter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        LOGGER.error(e.getError().getCode() + " + " + e.getError().getMessage(), e);
        return ResponseEntity.status(e.getError().getStatus())
                .body(e.getError().getCode() + " + " + e.getError().getMessage());
    }
}
