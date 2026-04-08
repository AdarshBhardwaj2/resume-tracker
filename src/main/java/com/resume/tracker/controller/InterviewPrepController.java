package com.resume.tracker.controller;

import com.resume.tracker.dto.InterviewPrepResponse;
import com.resume.tracker.service.InterviewPrepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview-prep")
public class InterviewPrepController {

    private final InterviewPrepService interviewPrepService;

    public InterviewPrepController(InterviewPrepService interviewPrepService) {
        this.interviewPrepService = interviewPrepService;
    }

    @GetMapping
    public InterviewPrepResponse getPrep(@RequestParam(defaultValue = "backend") String role) {
        return interviewPrepService.getPrep(role);
    }
}
