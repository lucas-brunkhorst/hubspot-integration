package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasbrunkhorst.hubspotintegration.events.WebhookEventListener;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookException;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookSignatureException;
import com.lucasbrunkhorst.hubspotintegration.record.WebhookEventDTO;
import com.lucasbrunkhorst.hubspotintegration.security.WebHookSignatureValidator;
import com.lucasbrunkhorst.hubspotintegration.service.impl.WebHookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebHookServiceImplTest {

    @Mock
    private WebHookSignatureValidator signatureValidator;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WebhookEventListener mockHandler;

    private WebHookServiceImpl webHookService;

    @BeforeEach
    void setup() {
        Map<String, WebhookEventListener> handlers = new HashMap<>();
        handlers.put("contact.creation", mockHandler);

        webHookService = new WebHookServiceImpl(signatureValidator, handlers, objectMapper);
    }

    @Test
    void shouldProcessWebhookSuccessfully() throws Exception {
        var rawBody = "[{\"subscriptionType\": \"contact.creation\"}]";
        var signature = "valid-signature";

        var mockEvent = new WebhookEventDTO(1L, 2L, 3L,
                4L, 5L, "contact.creation", 1,
                123456L, "API", "UPDATED");

        when(signatureValidator.isSignatureValid(rawBody, signature)).thenReturn(true);
        when(objectMapper.readValue(rawBody, WebhookEventDTO[].class)).thenReturn(new WebhookEventDTO[]{mockEvent});

        webHookService.processWebhook(rawBody, signature);

        verify(mockHandler).handleEvent(mockEvent);
    }

    @Test
    void shouldThrowExceptionWhenSignatureIsInvalid() {
        var rawBody = "qualquer";
        var signature = "invalid-signature";

        when(signatureValidator.isSignatureValid(rawBody, signature)).thenReturn(false);

        assertThrows(WebHookSignatureException.class, () -> {
            webHookService.processWebhook(rawBody, signature);
        });
    }

    @Test
    void shouldThrowExceptionWhenDeserializationFails() throws Exception {
        var rawBody = "invalid-json";
        var signature = "valid-signature";

        when(signatureValidator.isSignatureValid(rawBody, signature)).thenReturn(true);
        when(objectMapper.readValue(rawBody, WebhookEventDTO[].class)).thenThrow(new RuntimeException("erro JSON"));

        assertThrows(WebHookException.class, () -> {
            webHookService.processWebhook(rawBody, signature);
        });
    }

    @Test
    void shouldThrowExceptionWhenHandlerIsMissing() throws Exception {
        var rawBody = "[{\"subscriptionType\": \"contact.deletion\"}]";
        var signature = "valid-signature";

        var mockEvent = new WebhookEventDTO(1L, 2L, 3L,
                4L, 5L, "contact.deletion", 1,
                123456L, "API", "UPDATED");

        when(signatureValidator.isSignatureValid(rawBody, signature)).thenReturn(true);
        when(objectMapper.readValue(rawBody, WebhookEventDTO[].class)).thenReturn(new WebhookEventDTO[]{mockEvent});

        assertThrows(WebHookException.class, () -> {
            webHookService.processWebhook(rawBody, signature);
        });
    }
}