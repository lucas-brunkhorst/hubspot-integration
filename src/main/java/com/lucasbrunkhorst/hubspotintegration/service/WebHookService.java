package com.lucasbrunkhorst.hubspotintegration.service;

public interface WebHookService {
    void processWebhook(String rawBody, String signature);
}
