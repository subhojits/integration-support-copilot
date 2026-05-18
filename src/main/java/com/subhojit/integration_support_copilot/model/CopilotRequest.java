package com.subhojit.integration_support_copilot.model;

public class CopilotRequest {

    private String sourceSystem;
    private String targetSystem;
    private String adapterProtocol;
    private String businessImpact;
    private String integrationError;

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getAdapterProtocol() {
        return adapterProtocol;
    }

    public void setAdapterProtocol(String adapterProtocol) {
        this.adapterProtocol = adapterProtocol;
    }

    public String getBusinessImpact() {
        return businessImpact;
    }

    public void setBusinessImpact(String businessImpact) {
        this.businessImpact = businessImpact;
    }

    public String getIntegrationError() {
        return integrationError;
    }

    public void setIntegrationError(String integrationError) {
        this.integrationError = integrationError;
    }
}