package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.lucasbrunkhorst.hubspotintegration.client.HubSpotFeignClient;
import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotProperties;
import com.lucasbrunkhorst.hubspotintegration.exception.OAuthTokenException;
import com.lucasbrunkhorst.hubspotintegration.service.impl.OAuthTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OAuthTokenServiceImplTest {

    @Mock
    private Cache<String, String> hubSpotTokenCache;

    @Mock
    private HubSpotProperties hubSpotProperties;

    @Mock
    private HubSpotFeignClient hubSpotFeignClient;

    private OAuthTokenServiceImpl oauthTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        oauthTokenService = new OAuthTokenServiceImpl(hubSpotTokenCache, hubSpotProperties, hubSpotFeignClient);
    }

    @Test
    void testExchangeCodeForToken() {
        JsonNode mockResponse = mock(JsonNode.class);
        when(hubSpotFeignClient.exchangeToken(any())).thenReturn(mockResponse);
        when(mockResponse.has("access_token")).thenReturn(true);
        JsonNode accessTokenNode = mock(JsonNode.class);
        when(mockResponse.get("access_token")).thenReturn(accessTokenNode);
        when(accessTokenNode.asText()).thenReturn("mockAccessToken");

        HubSpotProperties.Client mockClient = mock(HubSpotProperties.Client.class);
        when(mockClient.getId()).thenReturn("mockClientId");
        when(hubSpotProperties.getClient()).thenReturn(mockClient);

        HubSpotProperties.Redirect mockRedirect = mock(HubSpotProperties.Redirect.class);
        when(mockRedirect.getUri()).thenReturn("mockRedirectUri");
        when(hubSpotProperties.getRedirect()).thenReturn(mockRedirect);

        oauthTokenService.exchangeCodeForToken("authorizationCode");

        verify(hubSpotTokenCache).put("access_token", "mockAccessToken");
    }

    @Test
    void testGetTokenWhenTokenExists() {
        when(hubSpotTokenCache.getIfPresent("access_token")).thenReturn("mockAccessToken");
        String token = oauthTokenService.getToken();

        assertEquals("mockAccessToken", token);
    }


    @Test
    void testGetTokenWhenTokenAndRefreshTokenAreMissingShouldThrowException() {
        when(hubSpotTokenCache.getIfPresent("access_token")).thenReturn(null);
        when(hubSpotTokenCache.getIfPresent("refresh_token")).thenReturn(null);

        OAuthTokenException exception = assertThrows(OAuthTokenException.class, () -> oauthTokenService.getToken());

        assertEquals(MessageConstants.REFRESH_TOKEN_MISSING, exception.getMessage());
    }

    @Test
    void testRefreshAccessTokenWhenRefreshTokenMissingShouldThrowException() {
        when(hubSpotTokenCache.getIfPresent("refresh_token")).thenReturn(null);

        OAuthTokenException exception = assertThrows(OAuthTokenException.class, () -> oauthTokenService.getToken());

        assertEquals(MessageConstants.REFRESH_TOKEN_MISSING, exception.getMessage());
    }

}
