package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.service.HubSpotOAuthService;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotOAuthServiceImpl;
import com.lucasbrunkhorst.hubspotintegration.service.OAuthTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    private final HubSpotOAuthService hubSpotOAuthService;
    private final OAuthTokenService authTokenService;

    public OAuthController(HubSpotOAuthServiceImpl hubSpotOAuthService, OAuthTokenService authTokenService) {
        this.hubSpotOAuthService = hubSpotOAuthService;
        this.authTokenService = authTokenService;
    }

    /**
     * Gera a URL de autorização para redirecionar o usuário à tela da HubSpot.
     */
    @GetMapping("/authorize")
    public ResponseEntity<String> generateAuthorizationUrl() {
        try {
            String authorizationUrl = hubSpotOAuthService.generateAuthorizationUrl();
            return ResponseEntity.ok(authorizationUrl);
        } catch (Exception e) {
            log.error("Erro ao gerar URL de autorização: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao gerar URL de autorização.");
        }
    }

    /**
     * Recebe o callback da HubSpot para o usuário.
     * Faz a troca do código por token e armazena no cache.
     */
    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam("code") String code) {
        try {
            authTokenService.exchangeCodeForToken(code);
            return ResponseEntity.ok("Token de acesso armazenado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao processar callback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao processar callback.");
        }
    }
}