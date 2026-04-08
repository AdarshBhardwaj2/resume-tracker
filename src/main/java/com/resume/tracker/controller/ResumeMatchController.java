package com.resume.tracker.controller;

import com.resume.tracker.dto.ResumeMatchRequest;
import com.resume.tracker.dto.ResumeMatchResponse;
import com.resume.tracker.service.ResumeMatchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resume-match")
public class ResumeMatchController {

    private final ResumeMatchService resumeMatchService;

    public ResumeMatchController(ResumeMatchService resumeMatchService) {
        this.resumeMatchService = resumeMatchService;
    }

    @PostMapping
    public ResumeMatchResponse analyze(@Valid @RequestBody ResumeMatchRequest request) {
        return resumeMatchService.analyze(request);
    }
}
