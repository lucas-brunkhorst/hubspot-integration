package com.lucasbrunkhorst.hubspotintegration.service;

import com.lucasbrunkhorst.hubspotintegration.config.HubSpotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class HubSpotOAuthService {

    private final HubSpotConfig hubSpotConfig;
    private final TokenService tokenService;

    public String generateAuthorizationUrl() {
        String encodedRedirectUri = URLEncoder.encode(hubSpotConfig.getRedirectUri(), StandardCharsets.UTF_8);
        String encodedScopes = URLEncoder.encode(hubSpotConfig.getScopes(), StandardCharsets.UTF_8);

        return String.format("%s?client_id=%s&scope=%s&redirect_uri=%s",
                hubSpotConfig.getAuthUrl(),
                hubSpotConfig.getClientId(),
                encodedScopes,
                encodedRedirectUri
        );
    }

    public void exchangeCodeForToken(String code) {
        tokenService.exchangeCodeForToken(hubSpotConfig, code);
    }
}
