package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.service.HubSpotOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final HubSpotOAuthService hubSpotOAuthService;

    /**
     * Gera a URL de autorização para redirecionar o usuário à tela da HubSpot.
     */
    @GetMapping("/authorize")
    public String generateAuthorizationUrl() {
        return hubSpotOAuthService.generateAuthorizationUrl();
    }

    /**
     * Recebe o callback da HubSpot para o usuário.
     * Faz a troca do código por token e armazena no cache.
     */
    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code) {
        try {
            hubSpotOAuthService.exchangeCodeForToken(code);
            return "Token de acesso armazenado com sucesso.";
        } catch (Exception e) {
            log.error("Erro ao processar callback: {}", e.getMessage(), e);
            return "Erro ao processar callback.";
        }
    }
}