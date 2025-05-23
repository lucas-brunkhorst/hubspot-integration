package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotOAuthService;
import com.lucasbrunkhorst.hubspotintegration.service.OAuthTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    public OAuthController(HubSpotOAuthService hubSpotOAuthService, OAuthTokenService authTokenService) {
        this.hubSpotOAuthService = hubSpotOAuthService;
        this.authTokenService = authTokenService;
    }

    @Operation(summary = "Gera URL de autorização para redirecionar o usuário à HubSpot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL de autorização gerada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro ao gerar URL de autorização.")
    })
    @GetMapping("/authorize")
    public ResponseEntity<String> generateAuthorizationUrl() {
        try {
            String authorizationUrl = hubSpotOAuthService.generateAuthorizationUrl();
            return ResponseEntity.ok(authorizationUrl);
        } catch (Exception e) {
            log.error("Erro ao gerar URL de autorização: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageConstants.AUTHORIZATION_URL_ERROR);
        }
    }

    @Operation(summary = "Recebe o callback da HubSpot para trocar código por token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de acesso armazenado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro ao processar callback.")
    })
    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam("code") String code) {
        try {
            authTokenService.exchangeCodeForToken(code);
            return ResponseEntity.ok(MessageConstants.TOKEN_STORED_SUCCESS);
        } catch (Exception e) {
            log.error("Erro ao processar callback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageConstants.CALLBACK_PROCESSING_ERROR);
        }
    }
}