package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotConfig;
import com.lucasbrunkhorst.hubspotintegration.exception.TokenException;
import com.lucasbrunkhorst.hubspotintegration.utils.HttpUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.isNull;

@Service
public class TokenService {

    private final Cache<String, String> tokenCache;
    private final RestTemplate restTemplate;
    private final HubSpotConfig hubSpotConfig;

    public TokenService(Cache<String, String> tokenCache, RestTemplate restTemplate, HubSpotConfig hubSpotConfig) {
        this.tokenCache = tokenCache;
        this.restTemplate = restTemplate;
        this.hubSpotConfig = hubSpotConfig;
    }

    /**
     * Troca o código de autorização por um token de acesso e armazena o token em cache.
     *
     * @param authorizationCode O código de autorização obtido após o redirecionamento.
     */
    public void exchangeCodeForToken(String authorizationCode) {
        String accessToken = exchangeAuthorizationCodeForToken(authorizationCode);
        storeTokenInCache(accessToken);
        System.out.println("Token armazenado com sucesso.");
    }

    /**
     * Obtém o token de acesso armazenado no cache.
     *
     * @return O token de acesso.
     * @throws RuntimeException Se não for possível obter o token.
     */
    public String getToken() {
        String accessToken = tokenCache.getIfPresent("access_token");

        if (isNull(accessToken)) {
            throw new TokenException("Token de acesso não disponível.");
        }

        return accessToken;
    }

    /**
     * Realiza a troca do código de autorização por um token de acesso.
     *
     * @param authorizationCode O código de autorização obtido após o redirecionamento.
     * @return O token de acesso obtido.
     */
    private String exchangeAuthorizationCodeForToken(String authorizationCode) {
        MultiValueMap<String, String> params = buildTokenRequestParams("authorization_code", authorizationCode);

        JsonNode response = postToHubSpotTokenEndpoint(params);
        return response.get("access_token").asText();
    }

    /**
     * Constrói os parâmetros necessários para a requisição de troca de token.
     *
     * @param grantType O tipo de grant, seja "authorization_code" ou "refresh_token".
     * @param token     O código de autorização ou o refresh token.
     * @return Os parâmetros de requisição.
     */
    private MultiValueMap<String, String> buildTokenRequestParams(String grantType, String token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", hubSpotConfig.getClientId());
        params.add("client_secret", hubSpotConfig.getClientSecret());

        if ("authorization_code".equals(grantType)) {
            params.add("code", token);
            params.add("redirect_uri", hubSpotConfig.getRedirectUri());
        }

        System.out.println("Parâmetros para requisição de token: " + params);

        return params;
    }

    /**
     * Realiza a requisição HTTP para o endpoint de token da HubSpot.
     *
     * @param params Os parâmetros a serem enviados na requisição.
     * @return A resposta da HubSpot em formato JSON.
     */
    private JsonNode postToHubSpotTokenEndpoint(MultiValueMap<String, String> params) {
        System.out.println("Enviando requisição para HubSpot: " + params);
        try {
            return HttpUtil.postForm(restTemplate, hubSpotConfig.getTokenUrl(), params);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com o HubSpot para obtenção de token: " + e.getMessage(), e);
        }
    }

    /**
     * Armazena o token de acesso em cache.
     *
     * @param accessToken O token de acesso.
     */
    private void storeTokenInCache(String accessToken) {
        tokenCache.put("access_token", accessToken);
    }
}
