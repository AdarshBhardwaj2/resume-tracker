package com.resume.tracker.dto;

import java.util.List;

public class ResumeMatchResponse {

    private int overallScore;
    private int skillsScore;
    private int atsReadinessScore;
    private int roleAlignmentScore;
    private String fitLabel;
    private String summary;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private List<String> strengths;
    private List<String> recommendations;

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public int getSkillsScore() {
        return skillsScore;
    }

    public void setSkillsScore(int skillsScore) {
        this.skillsScore = skillsScore;
    }

    public int getAtsReadinessScore() {
        return atsReadinessScore;
    }

    public void setAtsReadinessScore(int atsReadinessScore) {
        this.atsReadinessScore = atsReadinessScore;
    }

    public int getRoleAlignmentScore() {
        return roleAlignmentScore;
    }

    public void setRoleAlignmentScore(int roleAlignmentScore) {
        this.roleAlignmentScore = roleAlignmentScore;
    }

    public String getFitLabel() {
        return fitLabel;
    }

    public void setFitLabel(String fitLabel) {
        this.fitLabel = fitLabel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getMatchedKeywords() {
        return matchedKeywords;
    }

    public void setMatchedKeywords(List<String> matchedKeywords) {
        this.matchedKeywords = matchedKeywords;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
