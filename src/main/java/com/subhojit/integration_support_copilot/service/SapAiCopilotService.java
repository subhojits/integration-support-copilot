package com.subhojit.integration_support_copilot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhojit.integration_support_copilot.config.SapAiProperties;
import com.subhojit.integration_support_copilot.model.CopilotRequest;
import com.subhojit.integration_support_copilot.model.CopilotResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SapAiCopilotService {

    private final SapAiProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SapAiCopilotService(SapAiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public CopilotResponse analyzeError(CopilotRequest request) {
        try {
            String accessToken = fetchAccessToken();
            JsonNode completionResponse = callOrchestrationApi(accessToken, request);

            String answer = completionResponse
                    .path("final_result")
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (answer == null || answer.isBlank()) {
                return new CopilotResponse(
                        null,
                        "SAP AI Core returned a response, but no final answer text was found.");
            }

            return new CopilotResponse(answer, null);

        } catch (RestClientResponseException ex) {
            return new CopilotResponse(
                    null,
                    "Unable to analyze the error. SAP AI Core returned HTTP "
                            + ex.getStatusCode().value()
                            + " "
                            + ex.getStatusText()
                            + ". Response body: "
                            + ex.getResponseBodyAsString());

        } catch (Exception ex) {
            return new CopilotResponse(
                    null,
                    "Unable to analyze the error. Technical detail: " + ex.getMessage());
        }
    }

    private String fetchAccessToken() throws JsonProcessingException {
        String basicAuthValue = Base64.getEncoder().encodeToString(
                (properties.getClientId() + ":" + properties.getClientSecret())
                        .getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basicAuthValue);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                properties.getTokenUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class);

        String responseBody = responseEntity.getBody();

        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException("OAuth token API returned an empty response.");
        }

        JsonNode tokenResponse = objectMapper.readTree(responseBody);

        String accessToken = tokenResponse.path("access_token").asText();

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("OAuth token response did not contain access_token.");
        }

        return accessToken;
    }

    private JsonNode callOrchestrationApi(String accessToken, CopilotRequest request)
            throws JsonProcessingException {

        Map<String, Object> requestBodyMap = new LinkedHashMap<>();

        Map<String, String> configRef = new LinkedHashMap<>();
        configRef.put("id", properties.getOrchestrationConfigId());

        Map<String, String> placeholderValues = new LinkedHashMap<>();
        placeholderValues.put("source_system", safeValue(request.getSourceSystem()));
        placeholderValues.put("target_system", safeValue(request.getTargetSystem()));
        placeholderValues.put("adapter_protocol", safeValue(request.getAdapterProtocol()));
        placeholderValues.put("business_impact", safeValue(request.getBusinessImpact()));
        placeholderValues.put("integration_error", safeValue(request.getIntegrationError()));

        requestBodyMap.put("config_ref", configRef);
        requestBodyMap.put("placeholder_values", placeholderValues);

        String requestJson = objectMapper.writeValueAsString(requestBodyMap);

        System.out.println("========== SAP AI ORCHESTRATION REQUEST BODY ==========");
        System.out.println(requestJson);
        System.out.println("=======================================================");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("AI-Resource-Group", properties.getResourceGroup());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                properties.getCompletionUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class);

        String responseBody = responseEntity.getBody();

        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException("SAP Orchestration API returned an empty response.");
        }

        System.out.println("========== SAP AI ORCHESTRATION RAW RESPONSE ==========");
        System.out.println(responseBody);
        System.out.println("=======================================================");

        return objectMapper.readTree(responseBody);
    }

    private String safeValue(String value) {
        if (value == null || value.isBlank()) {
            return "Not provided";
        }
        return value.trim();
    }
}