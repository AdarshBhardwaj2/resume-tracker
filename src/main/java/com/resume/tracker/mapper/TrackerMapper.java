package com.resume.tracker.mapper;

import com.resume.tracker.dto.ApplicationResponse;
import com.resume.tracker.dto.EmailAnalysisResponse;
import com.resume.tracker.model.EmailAnalysis;
import com.resume.tracker.model.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class TrackerMapper {

    public ApplicationResponse toApplicationResponse(JobApplication application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setCompany(application.getCompany());
        response.setRole(application.getRole());
        response.setLocation(application.getLocation());
        response.setSource(application.getSource());
        response.setStatus(application.getStatus());
        response.setPriority(application.getPriority());
        response.setAppliedDate(application.getAppliedDate());
        response.setFollowUpDate(application.getFollowUpDate());
        response.setCompensation(application.getCompensation());
        response.setTechStack(application.getTechStack());
        response.setTags(application.getTags());
        response.setNotes(application.getNotes());
        response.setResumeFileName(application.getResumeFileName());
        if (application.getResumeStoredName() != null) {
            response.setResumeUrl("/files/" + application.getResumeStoredName());
        }
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        return response;
    }

    public EmailAnalysisResponse toEmailResponse(EmailAnalysis emailAnalysis) {
        EmailAnalysisResponse response = new EmailAnalysisResponse();
        response.setId(emailAnalysis.getId());
        response.setSender(emailAnalysis.getSender());
        response.setSubject(emailAnalysis.getSubject());
        response.setBody(emailAnalysis.getBody());
        response.setCategory(emailAnalysis.getCategory());
        response.setDetectedStage(emailAnalysis.getDetectedStage());
        response.setUrgency(emailAnalysis.getUrgency());
        response.setTone(emailAnalysis.getTone());
        response.setConfidenceScore(emailAnalysis.getConfidenceScore());
        response.setSummary(emailAnalysis.getSummary());
        response.setSuggestedAction(emailAnalysis.getSuggestedAction());
        if (emailAnalysis.getApplication() != null) {
            response.setApplicationId(emailAnalysis.getApplication().getId());
            response.setApplicationLabel(emailAnalysis.getApplication().getCompany() + " - " + emailAnalysis.getApplication().getRole());
        }
        response.setCreatedAt(emailAnalysis.getCreatedAt());
        return response;
    }
}
