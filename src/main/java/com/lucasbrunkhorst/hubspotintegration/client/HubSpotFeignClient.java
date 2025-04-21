package com.lucasbrunkhorst.hubspotintegration.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "hubspot-client", url = "${hubspot.api.base-url}")
public interface HubSpotFeignClient {

    @PostMapping(value = "/oauth/v1/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    JsonNode exchangeToken(@RequestBody MultiValueMap<String, String> formData);

    @PostMapping(value = "/crm/v3/objects/contacts", consumes = MediaType.APPLICATION_JSON_VALUE)
    JsonNode createContact(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, Object> payload
    );
}
