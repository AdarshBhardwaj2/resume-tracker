package com.resume.tracker.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsResponse {

    private long totalApplications;
    private long activePipelines;
    private long interviewsScheduled;
    private long followUpsDue;
    private long offersReceived;
    private long rejectionCount;
    private long applicationsThisWeek;
    private long staleApplications;
    private double responseRate;
    private double interviewRate;
    private List<String> topCompanies;
    private List<String> focusAreas;
    private Map<String, Long> statusBreakdown;
    private Map<String, Long> priorityBreakdown;
    private List<ApplicationResponse> recentApplications;
    private List<EmailAnalysisResponse> recentEmailInsights;

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getActivePipelines() {
        return activePipelines;
    }

    public void setActivePipelines(long activePipelines) {
        this.activePipelines = activePipelines;
    }

    public long getInterviewsScheduled() {
        return interviewsScheduled;
    }

    public void setInterviewsScheduled(long interviewsScheduled) {
        this.interviewsScheduled = interviewsScheduled;
    }

    public long getFollowUpsDue() {
        return followUpsDue;
    }

    public void setFollowUpsDue(long followUpsDue) {
        this.followUpsDue = followUpsDue;
    }

    public long getOffersReceived() {
        return offersReceived;
    }

    public void setOffersReceived(long offersReceived) {
        this.offersReceived = offersReceived;
    }

    public long getRejectionCount() {
        return rejectionCount;
    }

    public void setRejectionCount(long rejectionCount) {
        this.rejectionCount = rejectionCount;
    }

    public long getApplicationsThisWeek() {
        return applicationsThisWeek;
    }

    public void setApplicationsThisWeek(long applicationsThisWeek) {
        this.applicationsThisWeek = applicationsThisWeek;
    }

    public long getStaleApplications() {
        return staleApplications;
    }

    public void setStaleApplications(long staleApplications) {
        this.staleApplications = staleApplications;
    }

    public double getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(double responseRate) {
        this.responseRate = responseRate;
    }

    public double getInterviewRate() {
        return interviewRate;
    }

    public void setInterviewRate(double interviewRate) {
        this.interviewRate = interviewRate;
    }

    public List<String> getTopCompanies() {
        return topCompanies;
    }

    public void setTopCompanies(List<String> topCompanies) {
        this.topCompanies = topCompanies;
    }

    public List<String> getFocusAreas() {
        return focusAreas;
    }

    public void setFocusAreas(List<String> focusAreas) {
        this.focusAreas = focusAreas;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public Map<String, Long> getPriorityBreakdown() {
        return priorityBreakdown;
    }

    public void setPriorityBreakdown(Map<String, Long> priorityBreakdown) {
        this.priorityBreakdown = priorityBreakdown;
    }

    public List<ApplicationResponse> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(List<ApplicationResponse> recentApplications) {
        this.recentApplications = recentApplications;
    }

    public List<EmailAnalysisResponse> getRecentEmailInsights() {
        return recentEmailInsights;
    }

    public void setRecentEmailInsights(List<EmailAnalysisResponse> recentEmailInsights) {
        this.recentEmailInsights = recentEmailInsights;
    }
}
