package com.lucasbrunkhorst.hubspotintegration.events;

import com.lucasbrunkhorst.hubspotintegration.record.WebhookEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateWebhookEventListener implements WebhookEventListener {

    @Override
    public void getSubscriptionType(WebhookEventDTO event) {
        log.info("Evento de criação recebido: {}", event);
    }
}