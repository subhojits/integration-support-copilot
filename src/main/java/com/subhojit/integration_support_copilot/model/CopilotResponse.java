package com.subhojit.integration_support_copilot.model;

public class CopilotResponse {

    private String answer;
    private String error;

    // Required by Spring/Jackson
    public CopilotResponse() {
    }

    // Required by our service class
    public CopilotResponse(String answer, String error) {
        this.answer = answer;
        this.error = error;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}