package com.lucasbrunkhorst.hubspotintegration.security;

import com.lucasbrunkhorst.hubspotintegration.config.HubSpotProperties;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class WebHookSignatureValidator {

    private final HubSpotProperties config;

    public WebHookSignatureValidator(HubSpotProperties config) {
        this.config = config;
    }

    public boolean isSignatureValid(String rawBody, String receivedSignature) {
        try {
            String computedSignature = DigestUtils.sha256Hex(config.getClient().getSecret() + rawBody);

            return MessageDigest.isEqual(
                    computedSignature.getBytes(StandardCharsets.UTF_8),
                    receivedSignature.trim().getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar assinatura HMAC", e);
        }
    }
}