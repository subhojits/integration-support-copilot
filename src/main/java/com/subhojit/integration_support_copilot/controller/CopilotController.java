package com.subhojit.integration_support_copilot.controller;

import com.subhojit.integration_support_copilot.model.CopilotRequest;
import com.subhojit.integration_support_copilot.model.CopilotResponse;
import com.subhojit.integration_support_copilot.service.SapAiCopilotService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CopilotController {

    private final SapAiCopilotService sapAiCopilotService;

    public CopilotController(SapAiCopilotService sapAiCopilotService) {
        this.sapAiCopilotService = sapAiCopilotService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("copilotRequest", new CopilotRequest());
        return "copilot";
    }

    @PostMapping("/analyze")
    public String analyze(CopilotRequest copilotRequest, Model model) {
        CopilotResponse response = sapAiCopilotService.analyzeError(copilotRequest);

        model.addAttribute("copilotRequest", copilotRequest);
        model.addAttribute("copilotResponse", response);

        return "copilot";
    }
}