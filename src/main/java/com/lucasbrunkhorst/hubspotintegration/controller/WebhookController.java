package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.exception.WebHookException;
import com.lucasbrunkhorst.hubspotintegration.service.WebHookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebHookService webhookService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestHeader("X-HubSpot-Signature") String signature, @RequestBody String rawBody) {
        try {
            webhookService.processWebhook(rawBody, signature);
            return ResponseEntity.ok("Eventos processados com sucesso");
        } catch (WebHookException e) {
            log.error("Erro ao processar o webhook: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }
}

