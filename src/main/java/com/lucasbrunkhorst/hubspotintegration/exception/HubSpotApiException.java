package com.lucasbrunkhorst.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class HubSpotApiException extends ApiException {

    public HubSpotApiException(String message, String errorDetails) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorDetails);
    }
}
