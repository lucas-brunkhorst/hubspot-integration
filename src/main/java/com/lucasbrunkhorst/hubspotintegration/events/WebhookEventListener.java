package com.lucasbrunkhorst.hubspotintegration.events;

import com.lucasbrunkhorst.hubspotintegration.record.WebhookEventDTO;

public interface WebhookEventListener {
    void handleEvent(WebhookEventDTO event);
}
