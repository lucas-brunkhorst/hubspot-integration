package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.lucasbrunkhorst.hubspotintegration.client.HubSpotHttpClient;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotConfig;
import com.lucasbrunkhorst.hubspotintegration.exception.OAuthTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OAuthTokenServiceImpl implements OAuthTokenService {

    private final Cache<String, String> tokenCache;
    private final RestTemplate restTemplate;
    private final HubSpotConfig hubSpotConfig;
    private final HubSpotHttpClient hubSpotHttpClient;

    public OAuthTokenServiceImpl(Cache<String, String> tokenCache,
                                 RestTemplate restTemplate,
                                 HubSpotConfig hubSpotConfig,
                                 HubSpotHttpClient hubSpotHttpClient) {
        this.tokenCache = tokenCache;
        this.restTemplate = restTemplate;
        this.hubSpotConfig = hubSpotConfig;
        this.hubSpotHttpClient = hubSpotHttpClient;
    }

    @Override
    public void exchangeCodeForToken(String authorizationCode) {
        var accessToken = exchangeAuthorizationCodeForToken(authorizationCode);
        tokenCache.put("access_token", accessToken);
        log.info("Token armazenado com sucesso.");
    }

    @Override
    public String getToken() {
        final String accessToken = tokenCache.getIfPresent("access_token");

        if (accessToken == null) {
            throw new OAuthTokenException("Token de acesso não disponível.");
        }

        return accessToken;
    }

    private String exchangeAuthorizationCodeForToken(String authorizationCode) {
        final MultiValueMap<String, String> params = buildTokenRequestParams("authorization_code", authorizationCode);
        final JsonNode response = postToHubSpotTokenEndpoint(params);

        return response.get("access_token").asText();
    }

    private MultiValueMap<String, String> buildTokenRequestParams(String grantType, String token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", hubSpotConfig.getClientId());
        params.add("client_secret", hubSpotConfig.getClientSecret());

        if ("authorization_code".equals(grantType)) {
            params.add("code", token);
            params.add("redirect_uri", hubSpotConfig.getRedirectUri());
        }

        log.debug("Parâmetros da requisição de token: {}", params);
        return params;
    }

    private JsonNode postToHubSpotTokenEndpoint(MultiValueMap<String, String> params) {
        try {
            log.debug("Enviando requisição para endpoint de token da HubSpot");
            return hubSpotHttpClient.postForm(restTemplate, hubSpotConfig.getTokenUrl(), params);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com o HubSpot para obtenção de token: " + e.getMessage(), e);
        }
    }
}

