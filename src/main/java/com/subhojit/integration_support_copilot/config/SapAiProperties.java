package com.subhojit.integration_support_copilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sap.ai")
public class SapAiProperties {

    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String completionUrl;
    private String resourceGroup;
    private String orchestrationConfigId;

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

    public String getCompletionUrl() {
        return completionUrl;
    }

    public void setCompletionUrl(String completionUrl) {
        this.completionUrl = completionUrl;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
    }

    public String getOrchestrationConfigId() {
        return orchestrationConfigId;
    }

    public void setOrchestrationConfigId(String orchestrationConfigId) {
        this.orchestrationConfigId = orchestrationConfigId;
    }
}