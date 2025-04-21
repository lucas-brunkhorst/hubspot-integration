package com.lucasbrunkhorst.hubspotintegration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.lucasbrunkhorst.hubspotintegration.feign.HubSpotFeignClient;
import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotProperties;
import com.lucasbrunkhorst.hubspotintegration.exception.OAuthTokenException;
import com.lucasbrunkhorst.hubspotintegration.service.OAuthTokenService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Service
@Slf4j
public class OAuthTokenServiceImpl implements OAuthTokenService {

    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final Cache<String, String> hubSpotTokenCache;
    private final HubSpotProperties hubSpotProperties;
    private final HubSpotFeignClient hubSpotFeignClient;

    public OAuthTokenServiceImpl(Cache<String, String> hubSpotTokenCache,
                                 HubSpotProperties hubSpotProperties,
                                 HubSpotFeignClient hubSpotFeignClient) {
        this.hubSpotTokenCache = hubSpotTokenCache;
        this.hubSpotProperties = hubSpotProperties;
        this.hubSpotFeignClient = hubSpotFeignClient;
    }

    @Override
    public void exchangeCodeForToken(String authorizationCode) {
        log.info("Trocando authorization code por access token...");
        var response = requestToken("authorization_code", authorizationCode);

        storeTokens(response);
        log.info("Tokens armazenados com sucesso.");
    }

    @Override
    public String getToken() {
        return Optional.ofNullable(hubSpotTokenCache.getIfPresent(ACCESS_TOKEN_KEY))
                .or(() -> {
                    log.warn("Access token ausente ou expirado. Tentando renovação via refresh token...");
                    refreshAccessToken();
                    return Optional.ofNullable(hubSpotTokenCache.getIfPresent(ACCESS_TOKEN_KEY));
                })
                .orElseThrow(() -> new OAuthTokenException(MessageConstants.ACCESS_TOKEN_NOT_FOUND));
    }

    private void refreshAccessToken() {
        String refreshToken = hubSpotTokenCache.getIfPresent(REFRESH_TOKEN_KEY);

        if (refreshToken == null) {
            throw new OAuthTokenException(MessageConstants.REFRESH_TOKEN_MISSING);
        }

        try {
            var response = requestToken("refresh_token", refreshToken);
            storeTokens(response);
            log.info("Access token renovado com sucesso.");
        } catch (Exception e) {
            log.error("Erro inesperado ao renovar o access token", e);
            throw new OAuthTokenException(MessageConstants.TOKEN_RENEWAL_FAILED);
        }
    }

    private JsonNode requestToken(String grantType, String token) {
        MultiValueMap<String, String> params = buildTokenRequestParams(grantType, token);
        return postToHubSpotTokenEndpoint(params);
    }

    private MultiValueMap<String, String> buildTokenRequestParams(String grantType, String token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", hubSpotProperties.getClient().getId());
        params.add("client_secret", hubSpotProperties.getClient().getSecret());

        if ("authorization_code".equals(grantType)) {
            params.add("code", token);
            params.add("redirect_uri", hubSpotProperties.getRedirect().getUri());
        } else if ("refresh_token".equals(grantType)) {
            params.add("refresh_token", token);
        }

        log.debug("Parâmetros da requisição de token: {}", params);
        return params;
    }

    private JsonNode postToHubSpotTokenEndpoint(MultiValueMap<String, String> params) {
        try {
            return hubSpotFeignClient.exchangeToken(params);
        } catch (FeignException e) {
            throw new OAuthTokenException(MessageConstants.HUBSPOT_COMMUNICATION_ERROR + e.getMessage());
        } catch (Exception e) {
            throw new OAuthTokenException(MessageConstants.HUBSPOT_UNEXPECTED_ERROR + e.getMessage());
        }
    }

    private void storeTokens(JsonNode response) {
        if (response.has("access_token")) {
            hubSpotTokenCache.put(ACCESS_TOKEN_KEY, response.get("access_token").asText());
        }

        if (response.has("refresh_token")) {
            hubSpotTokenCache.put(REFRESH_TOKEN_KEY, response.get("refresh_token").asText());
        }
    }
}

