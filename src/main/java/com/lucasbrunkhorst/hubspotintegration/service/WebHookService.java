package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasbrunkhorst.hubspotintegration.events.WebhookEventListener;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookException;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookSignatureException;
import com.lucasbrunkhorst.hubspotintegration.record.WebhookEventDTO;
import com.lucasbrunkhorst.hubspotintegration.security.WebHookSignatureValidator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebHookService {

    private final WebHookSignatureValidator signatureValidator;
    private final Map<String, WebhookEventListener> webhookEventHandlers;
    private final ObjectMapper objectMapper;

    public WebHookService(WebHookSignatureValidator signatureValidator,
                          Map<String, WebhookEventListener> webhookEventHandlers,
                          ObjectMapper objectMapper) {

        this.signatureValidator = signatureValidator;
        this.webhookEventHandlers = webhookEventHandlers;
        this.objectMapper = objectMapper;
    }

    public void processWebhook(String rawBody, String signature) {
        if (!signatureValidator.isSignatureValid(rawBody, signature)) {
            throw new WebHookSignatureException("Assinatura inválida");
        }

        WebhookEventDTO[] events = deserializeEvents(rawBody);

        for (WebhookEventDTO event : events) {
            String eventType = event.subscriptionType();
            WebhookEventListener handler = webhookEventHandlers.get(eventType);
            if (handler == null) {
                throw new WebHookException("Handler não encontrado para o evento: " + eventType);
            }
            handler.getSubscriptionType(event);
        }
    }

    private WebhookEventDTO[] deserializeEvents(String rawBody) {
        try {
            return objectMapper.readValue(rawBody, WebhookEventDTO[].class);
        } catch (Exception e) {
            throw new WebHookException("Erro ao desserializar o corpo do webhook: " + e.getMessage());
        }
    }
}