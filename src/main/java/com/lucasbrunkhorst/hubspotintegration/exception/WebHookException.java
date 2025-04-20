package com.lucasbrunkhorst.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class WebHookException extends ApiException {

    public WebHookException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}