package com.lucasbrunkhorst.hubspotintegration.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasbrunkhorst.hubspotintegration.exception.RateLimitException;
import lombok.experimental.UtilityClass;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@UtilityClass
public class HttpUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Realiza uma requisição POST com o content-type x-www-form-urlencoded.
     */
    public static JsonNode postForm(RestTemplate restTemplate, String url, MultiValueMap<String, String> params) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao parsear resposta do HubSpot", e);
        }
    }

    /**
     * Realiza uma requisição POST com o content-type application/json e autenticação via Bearer Token.
     */
    public static JsonNode postJson(RestTemplate restTemplate, String url, Object body, String bearerToken) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return objectMapper.readTree(response.getBody());
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RateLimitException("Rate limit excedido.");
            }
            throw new RuntimeException("Erro na requisição: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado na requisição", e);
        }
    }
}
