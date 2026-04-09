package com.resume.tracker.service;

import com.resume.tracker.dto.EmailAnalysisRequest;
import com.resume.tracker.dto.EmailAnalysisResponse;
import com.resume.tracker.exception.ResourceNotFoundException;
import com.resume.tracker.mapper.TrackerMapper;
import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.model.EmailAnalysis;
import com.resume.tracker.model.JobApplication;
import com.resume.tracker.repository.EmailAnalysisRepository;
import com.resume.tracker.repository.JobApplicationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EmailAnalysisService {

    private final EmailAnalysisRepository emailAnalysisRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final TrackerMapper trackerMapper;

    public EmailAnalysisService(EmailAnalysisRepository emailAnalysisRepository,
                                JobApplicationRepository jobApplicationRepository,
                                TrackerMapper trackerMapper) {
        this.emailAnalysisRepository = emailAnalysisRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.trackerMapper = trackerMapper;
    }

    public List<EmailAnalysisResponse> getAll() {
        return emailAnalysisRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(trackerMapper::toEmailResponse)
                .toList();
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    public EmailAnalysisResponse analyze(EmailAnalysisRequest request) {
        String normalizedText = (request.getSubject() + " " + request.getBody()).toLowerCase(Locale.ENGLISH);

        EmailAnalysis emailAnalysis = new EmailAnalysis();
        emailAnalysis.setSender(request.getSender());
        emailAnalysis.setSubject(request.getSubject());
        emailAnalysis.setBody(request.getBody());
        emailAnalysis.setCategory(detectCategory(normalizedText));
        emailAnalysis.setDetectedStage(detectStage(normalizedText));
        emailAnalysis.setUrgency(detectUrgency(normalizedText));
        emailAnalysis.setTone(detectTone(normalizedText));
        emailAnalysis.setConfidenceScore(calculateConfidence(normalizedText));
        emailAnalysis.setSummary(generateSummary(request.getSubject(), normalizedText));
        emailAnalysis.setSuggestedAction(generateAction(normalizedText));

        if (request.getApplicationId() != null) {
            JobApplication application = jobApplicationRepository.findById(request.getApplicationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
            emailAnalysis.setApplication(application);
        }

        return trackerMapper.toEmailResponse(emailAnalysisRepository.save(emailAnalysis));
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    public void delete(Long id) {
        if (!emailAnalysisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Email analysis not found");
        }
        emailAnalysisRepository.deleteById(id);
    }

    private String detectCategory(String text) {
        if (containsAny(text, "interview", "round", "availability", "schedule")) {
            return "Interview";
        }
        if (containsAny(text, "offer", "compensation", "joining", "package")) {
            return "Offer";
        }
        if (containsAny(text, "assignment", "take-home", "assessment", "challenge")) {
            return "Assessment";
        }
        if (containsAny(text, "unfortunately", "regret", "moved forward")) {
            return "Rejection";
        }
        return "General Update";
    }

    private ApplicationStatus detectStage(String text) {
        if (containsAny(text, "offer", "joining", "compensation")) {
            return ApplicationStatus.OFFER;
        }
        if (containsAny(text, "interview", "round", "availability")) {
            return ApplicationStatus.INTERVIEW;
        }
        if (containsAny(text, "shortlisted", "selected", "assessment")) {
            return ApplicationStatus.SHORTLISTED;
        }
        if (containsAny(text, "unfortunately", "regret", "rejected")) {
            return ApplicationStatus.REJECTED;
        }
        return ApplicationStatus.APPLIED;
    }

    private String detectUrgency(String text) {
        if (containsAny(text, "today", "urgent", "asap", "immediately", "tomorrow")) {
            return "High";
        }
        if (containsAny(text, "this week", "soon", "within 3 days")) {
            return "Medium";
        }
        return "Low";
    }

    private String detectTone(String text) {
        if (containsAny(text, "happy", "glad", "congratulations", "pleased")) {
            return "Positive";
        }
        if (containsAny(text, "unfortunately", "regret", "sorry")) {
            return "Negative";
        }
        return "Neutral";
    }

    private int calculateConfidence(String text) {
        int score = 55;
        if (containsAny(text, "interview", "offer", "regret", "assessment", "availability")) {
            score += 20;
        }
        if (containsAny(text, "today", "tomorrow", "deadline", "confirm")) {
            score += 10;
        }
        if (text.length() > 150) {
            score += 8;
        }
        return Math.min(score, 96);
    }

    private String generateSummary(String subject, String text) {
        if (containsAny(text, "offer", "compensation")) {
            return "This email likely contains an offer-related update tied to compensation or onboarding details.";
        }
        if (containsAny(text, "interview", "availability")) {
            return "This email is asking you to coordinate an interview step and respond with timing availability.";
        }
        if (containsAny(text, "assignment", "assessment")) {
            return "This email includes a screening task that should be completed before the stated deadline.";
        }
        if (containsAny(text, "unfortunately", "regret")) {
            return "This email appears to communicate a rejection or a pause in the hiring process.";
        }
        return "This email is a general application update connected to: " + subject + ".";
    }

    private String generateAction(String text) {
        if (containsAny(text, "availability", "interview", "schedule")) {
            return "Reply with your available slots, confirm timezone, and prepare role-specific interview talking points.";
        }
        if (containsAny(text, "assignment", "assessment", "challenge")) {
            return "Block focused time, clarify submission format, and track the deadline in your follow-up list.";
        }
        if (containsAny(text, "offer", "compensation")) {
            return "Review the offer, compare with your target range, and prepare negotiation points if needed.";
        }
        if (containsAny(text, "unfortunately", "regret")) {
            return "Archive the application respectfully, note any feedback, and continue with other active pipelines.";
        }
        return "Log the update, decide whether a reply is required, and set a follow-up reminder if the message is time-sensitive.";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
