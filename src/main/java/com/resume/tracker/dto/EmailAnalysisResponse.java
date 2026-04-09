package com.resume.tracker.dto;

import com.resume.tracker.model.ApplicationStatus;

import java.time.LocalDateTime;

public class EmailAnalysisResponse {

    private Long id;
    private String sender;
    private String subject;
    private String body;
    private String category;
    private ApplicationStatus detectedStage;
    private String urgency;
    private String tone;
    private Integer confidenceScore;
    private String summary;
    private String suggestedAction;
    private Boolean actionRequired;
    private String recruiterIntent;
    private String responseWindow;
    private String nextStep;
    private String replyDraft;
    private String riskLevel;
    private Long applicationId;
    private String applicationLabel;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ApplicationStatus getDetectedStage() {
        return detectedStage;
    }

    public void setDetectedStage(ApplicationStatus detectedStage) {
        this.detectedStage = detectedStage;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }

    public Boolean getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(Boolean actionRequired) {
        this.actionRequired = actionRequired;
    }

    public String getRecruiterIntent() {
        return recruiterIntent;
    }

    public void setRecruiterIntent(String recruiterIntent) {
        this.recruiterIntent = recruiterIntent;
    }

    public String getResponseWindow() {
        return responseWindow;
    }

    public void setResponseWindow(String responseWindow) {
        this.responseWindow = responseWindow;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getReplyDraft() {
        return replyDraft;
    }

    public void setReplyDraft(String replyDraft) {
        this.replyDraft = replyDraft;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationLabel() {
        return applicationLabel;
    }

    public void setApplicationLabel(String applicationLabel) {
        this.applicationLabel = applicationLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
