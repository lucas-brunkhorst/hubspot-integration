package com.lucasbrunkhorst.hubspotintegration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucasbrunkhorst.hubspotintegration.feign.HubSpotFeignClient;
import com.lucasbrunkhorst.hubspotintegration.common.MessageConstants;
import com.lucasbrunkhorst.hubspotintegration.exception.HubSpotApiException;
import com.lucasbrunkhorst.hubspotintegration.record.ContactRequestDTO;
import com.lucasbrunkhorst.hubspotintegration.service.HubSpotContactService;
import com.lucasbrunkhorst.hubspotintegration.service.OAuthTokenService;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;
import java.util.Objects;

@Service
public class HubSpotContactServiceImpl implements HubSpotContactService {

    private final OAuthTokenService oAuthTokenService;
    private final HubSpotFeignClient hubSpotFeignClient;

    public HubSpotContactServiceImpl(OAuthTokenService oAuthTokenService, HubSpotFeignClient hubSpotFeignClient) {
        this.oAuthTokenService = oAuthTokenService;
        this.hubSpotFeignClient = hubSpotFeignClient;
    }

    @Retry(name = "contact.creation")
    public void createContact(@Valid ContactRequestDTO dto) {
        String token = oAuthTokenService.getToken();

        try {
            JsonNode response = hubSpotFeignClient.createContact("Bearer " + token, buildPayload(dto));

            if (response.has("status") && response.get("status").asText().equals("error")) {
                throw new HubSpotApiException(MessageConstants.CONTACT_CREATION_FAILED, response.toString());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                String retryAfter = Objects.requireNonNull(e.getResponseHeaders()).getFirst(HttpHeaders.RETRY_AFTER);
                throw new HubSpotApiException(MessageConstants.RATE_LIMIT_EXCEEDED + " Tente novamente após " + retryAfter + " segundos.", e.getResponseBodyAsString());
            }

            throw new HubSpotApiException(MessageConstants.HUBSPOT_CLIENT_ERROR + e.getMessage(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new HubSpotApiException(MessageConstants.CONTACT_UNEXPECTED_ERROR, e.getMessage());
        }
    }

    private Map<String, Object> buildPayload(ContactRequestDTO dto) {
        return Map.of("properties", Map.of(
                "email", dto.email(),
                "firstname", dto.firstname(),
                "lastname", dto.lastname(),
                "phone", dto.phone(),
                "company", dto.company()
        ));
    }
}