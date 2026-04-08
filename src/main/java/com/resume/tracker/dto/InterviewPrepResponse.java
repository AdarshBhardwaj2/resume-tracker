package com.resume.tracker.dto;

import java.util.List;

public class InterviewPrepResponse {

    private String role;
    private String focusSummary;
    private List<String> coreTopics;
    private List<String> topicDetails;
    private List<String> resumeChecklist;
    private List<String> likelyQuestions;
    private List<String> projectTalkingPoints;
    private List<String> finalRoundTips;
    private List<PrepResource> resources;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFocusSummary() {
        return focusSummary;
    }

    public void setFocusSummary(String focusSummary) {
        this.focusSummary = focusSummary;
    }

    public List<String> getCoreTopics() {
        return coreTopics;
    }

    public void setCoreTopics(List<String> coreTopics) {
        this.coreTopics = coreTopics;
    }

    public List<String> getResumeChecklist() {
        return resumeChecklist;
    }

    public void setResumeChecklist(List<String> resumeChecklist) {
        this.resumeChecklist = resumeChecklist;
    }

    public List<String> getTopicDetails() {
        return topicDetails;
    }

    public void setTopicDetails(List<String> topicDetails) {
        this.topicDetails = topicDetails;
    }

    public List<String> getLikelyQuestions() {
        return likelyQuestions;
    }

    public void setLikelyQuestions(List<String> likelyQuestions) {
        this.likelyQuestions = likelyQuestions;
    }

    public List<String> getProjectTalkingPoints() {
        return projectTalkingPoints;
    }

    public void setProjectTalkingPoints(List<String> projectTalkingPoints) {
        this.projectTalkingPoints = projectTalkingPoints;
    }

    public List<String> getFinalRoundTips() {
        return finalRoundTips;
    }

    public void setFinalRoundTips(List<String> finalRoundTips) {
        this.finalRoundTips = finalRoundTips;
    }

    public List<PrepResource> getResources() {
        return resources;
    }

    public void setResources(List<PrepResource> resources) {
        this.resources = resources;
    }

    public record PrepResource(String label, String description, String url) {
    }
}
