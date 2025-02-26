package com.uberaemos.ratelimiter.exception;

import org.springframework.http.HttpStatus;

public enum BusinessError {

    RULE_NOT_FOUND("100001", "Rule not found", HttpStatus.NOT_FOUND),
    UNKNOWN_ERROR("100002", "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    BusinessError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
