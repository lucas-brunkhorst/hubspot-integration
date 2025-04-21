package com.lucasbrunkhorst.hubspotintegration.service.impl;

import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotProperties;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotOAuthService;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class HubSpotOAuthServiceImpl implements HubSpotOAuthService {

    private static final String AUTHORIZATION_URL_TEMPLATE = "%s?client_id=%s&scope=%s&redirect_uri=%s";
    private final HubSpotProperties hubSpotProperties;

    public HubSpotOAuthServiceImpl(HubSpotProperties hubSpotProperties) {
        this.hubSpotProperties = hubSpotProperties;
    }

    public String generateAuthorizationUrl() {
        if (hubSpotProperties.getClient().getId() == null || hubSpotProperties.getScopes() == null || hubSpotProperties.getRedirect().getUri() == null) {
            throw new IllegalStateException(MessageConstants.INVALID_OAUTH_CONFIGURATION);
        }

        String encodedRedirectUri = URLEncoder.encode(hubSpotProperties.getRedirect().getUri(), StandardCharsets.UTF_8);
        String encodedScopes = URLEncoder.encode(hubSpotProperties.getScopes(), StandardCharsets.UTF_8);

        return String.format(AUTHORIZATION_URL_TEMPLATE,
                hubSpotProperties.getBase().getAuth().getUrl(),
                hubSpotProperties.getClient().getId(),
                encodedScopes,
                encodedRedirectUri
        );
    }
}
