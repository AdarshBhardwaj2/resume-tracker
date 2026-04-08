package com.resume.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class ResumeMatchRequest {

    @NotBlank
    private String targetRole;

    @NotBlank
    private String resumeText;

    @NotBlank
    private String jobDescription;

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
