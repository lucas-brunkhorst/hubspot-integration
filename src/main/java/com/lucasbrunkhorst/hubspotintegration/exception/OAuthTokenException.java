package com.lucasbrunkhorst.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class OAuthTokenException extends ApiException {

    public OAuthTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
