package com.subhojit.integration_support_copilot.model;

public class CpiFailedMessage {

    private String messageGuid;
    private String integrationFlowName;
    private String status;
    private String sender;
    private String receiver;
    private String logStart;
    private String logEnd;
    private String correlationId;
    private String applicationMessageId;

    public CpiFailedMessage() {
    }

    public CpiFailedMessage(
            String messageGuid,
            String integrationFlowName,
            String status,
            String sender,
            String receiver,
            String logStart,
            String logEnd,
            String correlationId,
            String applicationMessageId) {
        this.messageGuid = messageGuid;
        this.integrationFlowName = integrationFlowName;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
        this.logStart = logStart;
        this.logEnd = logEnd;
        this.correlationId = correlationId;
        this.applicationMessageId = applicationMessageId;
    }

    public String getMessageGuid() {
        return messageGuid;
    }

    public void setMessageGuid(String messageGuid) {
        this.messageGuid = messageGuid;
    }

    public String getIntegrationFlowName() {
        return integrationFlowName;
    }

    public void setIntegrationFlowName(String integrationFlowName) {
        this.integrationFlowName = integrationFlowName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getLogStart() {
        return logStart;
    }

    public void setLogStart(String logStart) {
        this.logStart = logStart;
    }

    public String getLogEnd() {
        return logEnd;
    }

    public void setLogEnd(String logEnd) {
        this.logEnd = logEnd;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getApplicationMessageId() {
        return applicationMessageId;
    }

    public void setApplicationMessageId(String applicationMessageId) {
        this.applicationMessageId = applicationMessageId;
    }
}