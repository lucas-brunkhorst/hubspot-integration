package com.lucasbrunkhorst.hubspotintegration.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public interface HubSpotHttpClient {
    JsonNode postForm(RestTemplate restTemplate, String url, MultiValueMap<String, String> params);
    JsonNode postJson(RestTemplate restTemplate, String url, Object body, String bearerToken);
}
