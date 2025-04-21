package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.exception.HubSpotApiException;
import com.lucasbrunkhorst.hubspotintegration.exception.OAuthTokenException;
import com.lucasbrunkhorst.hubspotintegration.record.ContactRequestDTO;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotContactService;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotContactServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contacts")
@Slf4j
public class ContactController {

    private final HubSpotContactService hubSpotContactService;

    public ContactController(HubSpotContactServiceImpl hubSpotContactService) {
        this.hubSpotContactService = hubSpotContactService;
    }

    @Operation(summary = "Cria um novo contato no HubSpot",
            description = "Este endpoint cria um novo contato na plataforma HubSpot a partir dos dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos."),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação. Token inválido ou expirado."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao tentar criar o contato na API do HubSpot.")
    })
    @PostMapping
    public ResponseEntity<String> createContact(@Valid @RequestBody ContactRequestDTO contactRequestDTO) {
        try {
            hubSpotContactService.createContact(contactRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Contato criado com sucesso.");
        } catch (OAuthTokenException e) {
            log.error("Erro de token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro de autenticação: " + e.getMessage());
        } catch (HubSpotApiException e) {
            log.error("Erro na API do HubSpot: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o contato na API do HubSpot");
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao processar a solicitação.");
        }
    }
}
