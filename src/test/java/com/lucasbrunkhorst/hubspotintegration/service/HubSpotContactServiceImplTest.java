package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasbrunkhorst.hubspotintegration.client.HubSpotFeignClient;
import com.lucasbrunkhorst.hubspotintegration.exception.HubSpotApiException;
import com.lucasbrunkhorst.hubspotintegration.record.ContactRequestDTO;
import com.lucasbrunkhorst.hubspotintegration.service.impl.HubSpotContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HubSpotContactServiceImplTest {

    @Mock
    private OAuthTokenService oAuthTokenService;

    @Mock
    private HubSpotFeignClient hubSpotFeignClient;

    private HubSpotContactServiceImpl hubSpotContactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hubSpotContactService = new HubSpotContactServiceImpl(oAuthTokenService, hubSpotFeignClient);
    }

    @Test
    void testCreateContactSuccess() {
        when(oAuthTokenService.getToken()).thenReturn("mockAccessToken");
        ContactRequestDTO dto = new ContactRequestDTO("email@domain.com", "Joao", "Silva", "123456789", "empresa");
        JsonNode mockResponse = mock(JsonNode.class);
        when(hubSpotFeignClient.createContact(eq("Bearer mockAccessToken"), any())).thenReturn(mockResponse);
        when(mockResponse.has("status")).thenReturn(false);
        hubSpotContactService.createContact(dto);
        verify(hubSpotFeignClient).createContact(eq("Bearer mockAccessToken"), any());
    }
    
    @Test
    void testCreateContactHubSpotApiExceptionUnexpectedError() {
        when(oAuthTokenService.getToken()).thenReturn("mockAccessToken");
        ContactRequestDTO dto = new ContactRequestDTO("email@domain.com", "Joao", "Silva", "123456789", "empresa");
        when(hubSpotFeignClient.createContact(eq("Bearer mockAccessToken"), any())).thenThrow(new RuntimeException("Unexpected error"));
        HubSpotApiException exception = assertThrows(HubSpotApiException.class, () -> hubSpotContactService.createContact(dto));
        assertEquals("Erro inesperado ao processar o contato.", exception.getMessage());
    }
}
