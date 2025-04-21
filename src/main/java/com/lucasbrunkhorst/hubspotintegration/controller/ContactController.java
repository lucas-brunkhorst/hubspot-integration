package com.lucasbrunkhorst.hubspotintegration.controller;

import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.exception.HubSpotApiException;
import com.lucasbrunkhorst.hubspotintegration.record.ContactRequestDTO;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotContactService;
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

    public ContactController(HubSpotContactService hubSpotContactService) {
        this.hubSpotContactService = hubSpotContactService;
    }

    @Operation(summary = "Cria um novo contato no HubSpot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos."),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação. Token inválido ou expirado."),
            @ApiResponse(responseCode = "429", description = "Rate limit excedido. Tente novamente mais tarde."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao tentar criar o contato na API do HubSpot.")
    })
    @PostMapping
    public ResponseEntity<String> createContact(@Valid @RequestBody ContactRequestDTO contactRequestDTO) {
        try {
            hubSpotContactService.createContact(contactRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(MessageConstants.CONTACT_CREATED_SUCCESS);
        } catch (HubSpotApiException e) {
            log.error("Erro na API do HubSpot: {}", e.getMessage());
            if (e.getMessage().contains("Limite de requisições excedido")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageConstants.HUBSPOT_API_ERROR);
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageConstants.UNEXPECTED_ERROR);
        }
    }
}