package com.subhojit.integration_support_copilot.model;

public class CpiErrorDetails {

    private String messageGuid;
    private String errorMessage;

    public CpiErrorDetails() {
    }

    public CpiErrorDetails(String messageGuid, String errorMessage) {
        this.messageGuid = messageGuid;
        this.errorMessage = errorMessage;
    }

    public String getMessageGuid() {
        return messageGuid;
    }

    public void setMessageGuid(String messageGuid) {
        this.messageGuid = messageGuid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}