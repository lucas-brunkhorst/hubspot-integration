package com.lucasbrunkhorst.hubspotintegration.config;

import com.lucasbrunkhorst.hubspotintegration.events.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebhookStrategyConfig {

    @Bean
    public Map<String, WebhookEventListener> webhookEventHandlers(
            CreateWebhookEventListener createHandler,
            UpdateWebhookEventListener updateHandler,
            DeleteWebhookEventListener deleteHandler) {

        Map<String, WebhookEventListener> handlers = new HashMap<>();
        handlers.put("contact.creation", createHandler);
        handlers.put("contact.propertyChange", updateHandler);
        handlers.put("contact.deletion", deleteHandler);
        return handlers;
    }
}