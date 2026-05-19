package com.subhojit.integration_support_copilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sap.cpi")
public class CpiProperties {

    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String baseUrl;
    private int failedMessageLimit;
    private String defaultTimeWindow;

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getFailedMessageLimit() {
        return failedMessageLimit;
    }

    public void setFailedMessageLimit(int failedMessageLimit) {
        this.failedMessageLimit = failedMessageLimit;
    }

    public String getDefaultTimeWindow() {
        return defaultTimeWindow;
    }

    public void setDefaultTimeWindow(String defaultTimeWindow) {
        this.defaultTimeWindow = defaultTimeWindow;
    }
}