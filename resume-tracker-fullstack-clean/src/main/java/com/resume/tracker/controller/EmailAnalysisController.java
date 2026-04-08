package com.resume.tracker.controller;

import com.resume.tracker.dto.EmailAnalysisRequest;
import com.resume.tracker.dto.EmailAnalysisResponse;
import com.resume.tracker.service.EmailAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class EmailAnalysisController {

    private final EmailAnalysisService emailAnalysisService;

    public EmailAnalysisController(EmailAnalysisService emailAnalysisService) {
        this.emailAnalysisService = emailAnalysisService;
    }

    @GetMapping
    public List<EmailAnalysisResponse> getAll() {
        return emailAnalysisService.getAll();
    }

    @PostMapping("/analyze")
    public EmailAnalysisResponse analyze(@Valid @RequestBody EmailAnalysisRequest request) {
        return emailAnalysisService.analyze(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        emailAnalysisService.delete(id);
    }
}
