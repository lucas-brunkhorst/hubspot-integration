package com.lucasbrunkhorst.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorDetails;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorDetails = null;
    }

    public ApiException(String message, HttpStatus status, String errorDetails) {
        super(message);
        this.status = status;
        this.errorDetails = errorDetails;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
