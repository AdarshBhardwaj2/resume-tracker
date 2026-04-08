package com.resume.tracker.controller;

import com.resume.tracker.dto.ApplicationRequest;
import com.resume.tracker.dto.ApplicationResponse;
import com.resume.tracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public List<ApplicationResponse> getAll(@RequestParam(required = false) String status,
                                            @RequestParam(required = false) String keyword) {
        return jobApplicationService.getAll(status, keyword);
    }

    @GetMapping("/{id}")
    public ApplicationResponse getOne(@PathVariable Long id) {
        return jobApplicationService.getOne(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse create(@Valid @RequestPart("application") ApplicationRequest request,
                                      @RequestPart(value = "resume", required = false) MultipartFile resume) {
        return jobApplicationService.create(request, resume);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse update(@PathVariable Long id,
                                      @Valid @RequestPart("application") ApplicationRequest request,
                                      @RequestPart(value = "resume", required = false) MultipartFile resume) {
        return jobApplicationService.update(id, request, resume);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        jobApplicationService.delete(id);
    }
}
