package com.lucasbrunkhorst.hubspotintegration.service;

import com.lucasbrunkhorst.hubspotintegration.config.HubSpotConfig;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class HubSpotOAuthServiceImpl implements HubSpotOAuthService {

    private static final String AUTHORIZATION_URL_TEMPLATE = "%s?client_id=%s&scope=%s&redirect_uri=%s";
    private final HubSpotConfig hubSpotConfig;

    public HubSpotOAuthServiceImpl(HubSpotConfig hubSpotConfig) {
        this.hubSpotConfig = hubSpotConfig;
    }

    public String generateAuthorizationUrl() {
        if (hubSpotConfig.getClientId() == null || hubSpotConfig.getScopes() == null || hubSpotConfig.getRedirectUri() == null) {
            throw new IllegalStateException("Os parametros clientId, scopes ou redirectUri não estao configurados corretamente.");
        }

        String encodedRedirectUri = URLEncoder.encode(hubSpotConfig.getRedirectUri(), StandardCharsets.UTF_8);
        String encodedScopes = URLEncoder.encode(hubSpotConfig.getScopes(), StandardCharsets.UTF_8);

        return String.format(AUTHORIZATION_URL_TEMPLATE,
                hubSpotConfig.getAuthUrl(),
                hubSpotConfig.getClientId(),
                encodedScopes,
                encodedRedirectUri
        );
    }
}
