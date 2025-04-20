package com.lucasbrunkhorst.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class TokenException extends ApiException {

    public TokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
