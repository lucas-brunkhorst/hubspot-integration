package com.lucasbrunkhorst.hubspotintegration.service;

import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.config.HubSpotProperties;
import com.lucasbrunkhorst.hubspotintegration.service.impl.HubSpotOAuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HubSpotOAuthServiceImplTest {

    @Mock
    private HubSpotProperties hubSpotProperties;

    private HubSpotOAuthServiceImpl hubSpotOAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hubSpotOAuthService = new HubSpotOAuthServiceImpl(hubSpotProperties);
    }

    @Test
    void testGenerateAuthorizationUrlShouldReturnUrlWhenValidConfiguration() {
        HubSpotProperties.Base.Auth mockAuth = mock(HubSpotProperties.Base.Auth.class);
        when(mockAuth.getUrl()).thenReturn("https://auth.hubspot.com/oauth/authorize");

        HubSpotProperties.Base mockBase = mock(HubSpotProperties.Base.class);
        when(mockBase.getAuth()).thenReturn(mockAuth);

        HubSpotProperties.Redirect mockRedirect = mock(HubSpotProperties.Redirect.class);
        when(mockRedirect.getUri()).thenReturn("http://localhost:8080/oauth/callback");

        HubSpotProperties.Client mockClient = mock(HubSpotProperties.Client.class);
        when(mockClient.getId()).thenReturn("mockClientId");

        when(hubSpotProperties.getClient()).thenReturn(mockClient);
        when(hubSpotProperties.getScopes()).thenReturn("contacts");
        when(hubSpotProperties.getRedirect()).thenReturn(mockRedirect);
        when(hubSpotProperties.getBase()).thenReturn(mockBase);

        HubSpotOAuthServiceImpl oauthService = new HubSpotOAuthServiceImpl(hubSpotProperties);
        var authorizationUrl = oauthService.generateAuthorizationUrl();

        var expectedUrl = "https://auth.hubspot.com/oauth/authorize?client_id=mockClientId&scope=contacts&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth%2Fcallback";
        assertEquals(expectedUrl, authorizationUrl);
    }

    @Test
    void testGenerateAuthorizationUrlShouldThrowExceptionWhenConfigurationIsMissing() {
        HubSpotProperties.Client mockClient = mock(HubSpotProperties.Client.class);
        when(hubSpotProperties.getClient()).thenReturn(mockClient);
        when(hubSpotProperties.getScopes()).thenReturn("contacts");
        when(hubSpotProperties.getRedirect()).thenReturn(mock(HubSpotProperties.Redirect.class));

        var exception = assertThrows(IllegalStateException.class, () -> hubSpotOAuthService.generateAuthorizationUrl());
        assertEquals(MessageConstants.INVALID_OAUTH_CONFIGURATION, exception.getMessage());
    }
}
