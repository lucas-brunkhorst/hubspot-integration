package com.lucasbrunkhorst.hubspotintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Getter
@Configuration
public class HubSpotConfig {

    @Value("${hubspot.client.id}")
    private String clientId;

    @Value("${hubspot.client.secret}")
    private String clientSecret;

    @Value("${hubspot.redirect.uri}")
    private String redirectUri;

    @Value("${hubspot.scopes}")
    private String scopes;

    @Value("${hubspot.base.auth.url:https://app.hubspot.com/oauth/authorize}")
    private String authUrl;

    @Value("${hubspot.token.url:https://api.hubapi.com/oauth/v1/token}")
    private String tokenUrl;

    @Value("${hubspot.contact.url:https://api.hubapi.com/crm/v3/objects/contacts}")
    private String contactUrl;

    @Value("${hubspot.webhook.secret}")
    private String webhookSecret;
}