package com.lucasbrunkhorst.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasbrunkhorst.hubspotintegration.client.HubSpotHttpClient;
import com.lucasbrunkhorst.hubspotintegration.exception.HubSpotApiException;
import com.lucasbrunkhorst.hubspotintegration.record.ContactRequestDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class HubSpotContactServiceImpl implements HubSpotContactService {

    private static final String HUBSPOT_API_URL = "https://api.hubapi.com/crm/v3/objects/contacts";
    private final OAuthTokenService oAuthTokenService;
    private final RestTemplate restTemplate;
    private final HubSpotHttpClient hubSpotHttpClient;

    public HubSpotContactServiceImpl(OAuthTokenService oAuthTokenService, RestTemplate restTemplate, HubSpotHttpClient hubSpotHttpClient) {
        this.oAuthTokenService = oAuthTokenService;
        this.restTemplate = restTemplate;
        this.hubSpotHttpClient = hubSpotHttpClient;
    }

    public void createContact(@Valid ContactRequestDTO dto) {
        String token = oAuthTokenService.getToken();

        try {
            JsonNode response = hubSpotHttpClient.postJson(restTemplate, HUBSPOT_API_URL, buildPayload(dto), token);

            if (response.has("status") && response.get("status").asText().equals("error")) {
                throw new HubSpotApiException("Erro ao criar contato no HubSpot", response.toString());
            }

        } catch (HttpClientErrorException e) {
            throw new HubSpotApiException("Erro ao conectar com a API do HubSpot: " + e.getMessage(), e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new HubSpotApiException("Erro no servidor do HubSpot: " + e.getMessage(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new HubSpotApiException("Erro inesperado ao processar o contato.", e.getMessage());
        }
    }

    private Map<String, Object> buildPayload(ContactRequestDTO dto) {
        Map<String, Object> properties = Map.of(
                "email", dto.email(),
                "firstname", dto.firstname(),
                "lastname", dto.lastname(),
                "phone", dto.phone(),
                "company", dto.company()
        );
        return Map.of("properties", properties);
    }
}
