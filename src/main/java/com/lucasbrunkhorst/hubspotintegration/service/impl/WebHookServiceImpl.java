package com.lucasbrunkhorst.hubspotintegration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.events.WebhookEventListener;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookException;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookSignatureException;
import com.lucasbrunkhorst.hubspotintegration.record.WebhookEventDTO;
import com.lucasbrunkhorst.hubspotintegration.security.WebHookSignatureValidator;
import com.lucasbrunkhorst.hubspotintegration.service.WebHookService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebHookServiceImpl implements WebHookService {

    private final WebHookSignatureValidator signatureValidator;
    private final Map<String, WebhookEventListener> webhookEventHandlers;
    private final ObjectMapper objectMapper;

    public WebHookServiceImpl(WebHookSignatureValidator signatureValidator,
                              Map<String, WebhookEventListener> webhookEventHandlers,
                              ObjectMapper objectMapper) {
        this.signatureValidator = signatureValidator;
        this.webhookEventHandlers = webhookEventHandlers;
        this.objectMapper = objectMapper;
    }

    public void processWebhook(String rawBody, String signature) {
        if (!signatureValidator.isSignatureValid(rawBody, signature)) {
            throw new WebHookSignatureException(MessageConstants.INVALID_WEBHOOK_SIGNATURE);
        }

        WebhookEventDTO[] events = deserializeEvents(rawBody);
        for (WebhookEventDTO event : events) {
            String eventType = event.subscriptionType();
            WebhookEventListener handler = webhookEventHandlers.get(eventType);
            if (handler == null) {
                throw new WebHookException(MessageConstants.WEBHOOK_HANDLER_NOT_FOUND + eventType);
            }
            handler.handleEvent(event);
        }
    }

    private WebhookEventDTO[] deserializeEvents(String rawBody) {
        try {
            return objectMapper.readValue(rawBody, WebhookEventDTO[].class);
        } catch (Exception e) {
            throw new WebHookException(MessageConstants.WEBHOOK_DESERIALIZATION_ERROR + e.getMessage());
        }
    }
}