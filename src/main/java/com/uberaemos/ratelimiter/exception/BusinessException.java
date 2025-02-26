package com.uberaemos.ratelimiter.exception;

public class BusinessException extends RuntimeException {
    private final BusinessError error;

    public BusinessException(BusinessError error) {
        super(error.getMessage());
        this.error = error;
    }

    public BusinessError getError() {
        return error;
    }
}
