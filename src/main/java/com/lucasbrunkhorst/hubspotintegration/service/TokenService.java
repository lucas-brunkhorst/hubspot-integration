package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotConfig;
import com.lucasbrunkhorst.hubspotintegration.utils.HttpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final Cache<String, String> tokenCache;
    private final RestTemplate restTemplate = new RestTemplate();

    public void exchangeCodeForToken(HubSpotConfig hubSpotConfig, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", hubSpotConfig.getClientId());
        params.add("client_secret", hubSpotConfig.getClientSecret());
        params.add("redirect_uri", hubSpotConfig.getRedirectUri());
        params.add("code", code);

        JsonNode response = HttpUtil.postForm(restTemplate, hubSpotConfig.getTokenUrl(), params);
        var accessToken = response.get("access_token").asText();
        tokenCache.put("access_token", accessToken);
    }

    public String getToken() {
        return tokenCache.getIfPresent("access_token");
    }
}