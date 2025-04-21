package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.exception.WebHookException;
import com.lucasbrunkhorst.hubspotintegration.service.WebHookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Processa eventos recebidos do HubSpot via Webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos processados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de assinatura inválida ou erro de processamento dos eventos.")
    })
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestHeader("X-HubSpot-Signature") String signature, @RequestBody String rawBody) {
        try {
            webhookService.processWebhook(rawBody, signature);
            return ResponseEntity.ok(MessageConstants.EVENTS_PROCESSED_SUCCESS);
        } catch (WebHookException e) {
            log.error("Erro ao processar o webhook: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }
}

