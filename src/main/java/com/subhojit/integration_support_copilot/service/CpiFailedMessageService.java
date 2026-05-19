package com.subhojit.integration_support_copilot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.subhojit.integration_support_copilot.config.CpiProperties;
import com.subhojit.integration_support_copilot.model.CpiErrorDetails;
import com.subhojit.integration_support_copilot.model.CpiFailedMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class CpiFailedMessageService {

    private static final DateTimeFormatter CPI_DATETIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final CpiProperties cpiProperties;
    private final RestClient restClient;

    public CpiFailedMessageService(CpiProperties cpiProperties) {
        this.cpiProperties = cpiProperties;
        this.restClient = RestClient.create();
    }

    /**
     * Fetches the latest failed CPI message logs for the requested time window.
     *
     * Supported values:
     * - 1h
     * - 24h
     * - 2d
     * - 5d
     * - 7d
     */
    public List<CpiFailedMessage> getRecentFailedMessages(String timeWindow) {
        String accessToken = fetchAccessToken();
        String filterExpression = buildFailedMessageFilter(timeWindow);
        URI requestUri = buildMessageProcessingLogsUri(filterExpression);

        JsonNode response = restClient.get()
                .uri(requestUri)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(JsonNode.class);

        return extractFailedMessages(response);
    }

    /**
     * Fetches the detailed error information for one selected CPI failed message.
     */
    public CpiErrorDetails getErrorDetailsForMessage(String messageGuid) {
        if (messageGuid == null || messageGuid.isBlank()) {
            throw new IllegalArgumentException("Message GUID must not be empty.");
        }

        String accessToken = fetchAccessToken();
        URI requestUri = buildErrorInformationUri(messageGuid);

        String errorMessage = restClient.get()
                .uri(requestUri)
                .header("Authorization", "Bearer " + accessToken)
                .accept(
                        MediaType.TEXT_PLAIN,
                        MediaType.APPLICATION_OCTET_STREAM,
                        MediaType.ALL)
                .retrieve()
                .body(String.class);

        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = "No detailed CPI error information was returned for this message.";
        }

        return new CpiErrorDetails(messageGuid, errorMessage.trim());
    }

    private String fetchAccessToken() {
        String clientCredentials = cpiProperties.getClientId()
                + ":"
                + cpiProperties.getClientSecret();

        String basicAuthValue = Base64.getEncoder()
                .encodeToString(clientCredentials.getBytes(StandardCharsets.UTF_8));

        JsonNode tokenResponse = restClient.post()
                .uri(cpiProperties.getTokenUrl())
                .header("Authorization", "Basic " + basicAuthValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials")
                .retrieve()
                .body(JsonNode.class);

        if (tokenResponse == null) {
            throw new IllegalStateException("CPI OAuth token response was empty.");
        }

        String accessToken = tokenResponse.path("access_token").asText();

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "CPI OAuth token response did not contain access_token.");
        }

        return accessToken;
    }

    private String buildFailedMessageFilter(String timeWindow) {
        LocalDateTime fromDateTimeUtc = calculateFromDateTimeUtc(timeWindow);
        String formattedDateTime = CPI_DATETIME_FORMATTER.format(fromDateTimeUtc);

        return "Status eq 'FAILED' and LogEnd gt datetime'" + formattedDateTime + "'";
    }

    private LocalDateTime calculateFromDateTimeUtc(String timeWindow) {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);

        if (timeWindow == null || timeWindow.isBlank()) {
            timeWindow = cpiProperties.getDefaultTimeWindow();
        }

        return switch (timeWindow.toLowerCase()) {
            case "1h" -> nowUtc.minusHours(1);
            case "24h" -> nowUtc.minusHours(24);
            case "2d" -> nowUtc.minusDays(2);
            case "5d" -> nowUtc.minusDays(5);
            case "7d" -> nowUtc.minusDays(7);
            default -> nowUtc.minusHours(24);
        };
    }

    private URI buildMessageProcessingLogsUri(String filterExpression) {
        String baseUrl = removeTrailingSlash(cpiProperties.getBaseUrl());

        return UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/v1/MessageProcessingLogs")
                .queryParam("$format", "json")
                .queryParam("$top", cpiProperties.getFailedMessageLimit())
                .queryParam("$orderby", "LogEnd desc")
                .queryParam("$filter", filterExpression)
                .build()
                .encode()
                .toUri();
    }

    private URI buildErrorInformationUri(String messageGuid) {
        String baseUrl = removeTrailingSlash(cpiProperties.getBaseUrl());

        String encodedMessageGuid = UriUtils.encodePathSegment(messageGuid, StandardCharsets.UTF_8);

        String url = baseUrl
                + "/api/v1/MessageProcessingLogErrorInformations('"
                + encodedMessageGuid
                + "')/$value";

        return URI.create(url);
    }

    private List<CpiFailedMessage> extractFailedMessages(JsonNode response) {
        List<CpiFailedMessage> failedMessages = new ArrayList<>();

        if (response == null) {
            return failedMessages;
        }

        JsonNode resultsNode = findResultsNode(response);

        if (resultsNode == null || !resultsNode.isArray()) {
            return failedMessages;
        }

        for (JsonNode item : resultsNode) {
            CpiFailedMessage failedMessage = new CpiFailedMessage();

            failedMessage.setMessageGuid(readText(item, "MessageGuid"));
            failedMessage.setIntegrationFlowName(readText(item, "IntegrationFlowName"));
            failedMessage.setStatus(readText(item, "Status"));
            failedMessage.setSender(readText(item, "Sender"));
            failedMessage.setReceiver(readText(item, "Receiver"));
            failedMessage.setLogStart(readText(item, "LogStart"));
            failedMessage.setLogEnd(readText(item, "LogEnd"));
            failedMessage.setCorrelationId(readText(item, "CorrelationId"));
            failedMessage.setApplicationMessageId(readText(item, "ApplicationMessageId"));

            failedMessages.add(failedMessage);
        }

        return failedMessages;
    }

    private JsonNode findResultsNode(JsonNode response) {
        JsonNode odataV2Results = response.path("d").path("results");
        if (odataV2Results.isArray()) {
            return odataV2Results;
        }

        JsonNode valueResults = response.path("value");
        if (valueResults.isArray()) {
            return valueResults;
        }

        return null;
    }

    private String readText(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);

        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return "";
        }

        return fieldNode.asText("");
    }

    private String removeTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}