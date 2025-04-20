package com.lucasbrunkhorst.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class WebHookSignatureException extends ApiException {
    public WebHookSignatureException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
