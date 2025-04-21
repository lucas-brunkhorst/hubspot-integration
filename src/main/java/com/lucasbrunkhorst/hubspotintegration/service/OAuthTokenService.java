package com.lucasbrunkhorst.hubspotintegration.service;

public interface OAuthTokenService {
    void exchangeCodeForToken(String authorizationCode);
    String getToken();
}
