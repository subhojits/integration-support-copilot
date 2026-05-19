package com.subhojit.integration_support_copilot.controller;

import com.subhojit.integration_support_copilot.model.CopilotRequest;
import com.subhojit.integration_support_copilot.model.CopilotResponse;
import com.subhojit.integration_support_copilot.model.CpiErrorDetails;
import com.subhojit.integration_support_copilot.model.CpiFailedMessage;
import com.subhojit.integration_support_copilot.service.CpiFailedMessageService;
import com.subhojit.integration_support_copilot.service.SapAiCopilotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CopilotController {

    private final SapAiCopilotService sapAiCopilotService;
    private final CpiFailedMessageService cpiFailedMessageService;

    public CopilotController(
            SapAiCopilotService sapAiCopilotService,
            CpiFailedMessageService cpiFailedMessageService) {
        this.sapAiCopilotService = sapAiCopilotService;
        this.cpiFailedMessageService = cpiFailedMessageService;
    }

    /**
     * Existing home page.
     * Displays the Integration Support Copilot form.
     */
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("copilotRequest", new CopilotRequest());
        return "copilot";
    }

    /**
     * Existing AI analysis action.
     * Sends the entered/copied issue details to SAP AI orchestration.
     */
    @PostMapping("/analyze")
    public String analyze(CopilotRequest copilotRequest, Model model) {
        CopilotResponse response = sapAiCopilotService.analyzeError(copilotRequest);

        model.addAttribute("copilotRequest", copilotRequest);
        model.addAttribute("copilotResponse", response);

        return "copilot";
    }

    /**
     * New API endpoint:
     * Returns the latest failed CPI messages for a selected time window.
     *
     * Example:
     * GET /cpi/errors?window=24h
     *
     * Supported window values:
     * - 1h
     * - 24h
     * - 2d
     */
    @GetMapping("/cpi/errors")
    @ResponseBody
    public ResponseEntity<?> getRecentCpiErrors(
            @RequestParam(defaultValue = "24h") String window) {
        try {
            List<CpiFailedMessage> failedMessages = cpiFailedMessageService.getRecentFailedMessages(window);

            return ResponseEntity.ok(failedMessages);

        } catch (Exception ex) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to fetch failed CPI messages.",
                    ex.getMessage());
        }
    }

    /**
     * New API endpoint:
     * Returns detailed CPI error text for a selected failed message.
     *
     * Example:
     * GET /cpi/errors/{messageGuid}/details
     */
    @GetMapping("/cpi/errors/{messageGuid}/details")
    @ResponseBody
    public ResponseEntity<?> getCpiErrorDetails(
            @PathVariable String messageGuid) {
        try {
            CpiErrorDetails errorDetails = cpiFailedMessageService.getErrorDetailsForMessage(messageGuid);

            return ResponseEntity.ok(errorDetails);

        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Invalid CPI message GUID.",
                    ex.getMessage());

        } catch (Exception ex) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to fetch detailed CPI error information.",
                    ex.getMessage());
        }
    }

    /**
     * Small helper to return structured JSON error responses.
     * This will help the UI show useful messages later in Step 8E.
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(
            HttpStatus status,
            String message,
            String technicalDetail) {
        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put("message", message);
        errorResponse.put(
                "technicalDetail",
                technicalDetail == null || technicalDetail.isBlank()
                        ? "No additional technical detail available."
                        : technicalDetail);

        return ResponseEntity.status(status).body(errorResponse);
    }
}