package com.resume.tracker.service;

import com.resume.tracker.dto.EmailAnalysisRequest;
import com.resume.tracker.dto.EmailAnalysisResponse;
import com.resume.tracker.exception.ResourceNotFoundException;
import com.resume.tracker.mapper.TrackerMapper;
import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.model.EmailAnalysis;
import com.resume.tracker.model.JobApplication;
import com.resume.tracker.model.User;
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
    private final CurrentUserService currentUserService;

    public EmailAnalysisService(EmailAnalysisRepository emailAnalysisRepository,
                                JobApplicationRepository jobApplicationRepository,
                                TrackerMapper trackerMapper,
                                CurrentUserService currentUserService) {
        this.emailAnalysisRepository = emailAnalysisRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.trackerMapper = trackerMapper;
        this.currentUserService = currentUserService;
    }

    public List<EmailAnalysisResponse> getAll() {
        return emailAnalysisRepository.findAllByOwnerOrderByCreatedAtDesc(currentUserService.getCurrentUser()).stream()
                .map(trackerMapper::toEmailResponse)
                .toList();
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    public EmailAnalysisResponse analyze(EmailAnalysisRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        String normalizedText = (request.getSubject() + " " + request.getBody()).toLowerCase(Locale.ENGLISH);

        EmailAnalysis emailAnalysis = new EmailAnalysis();
        emailAnalysis.setOwner(currentUser);
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
        emailAnalysis.setActionRequired(isActionRequired(normalizedText));
        emailAnalysis.setRecruiterIntent(detectIntent(normalizedText));
        emailAnalysis.setResponseWindow(detectResponseWindow(normalizedText));
        emailAnalysis.setNextStep(generateNextStep(normalizedText));
        emailAnalysis.setReplyDraft(generateReplyDraft(request.getSubject(), normalizedText));
        emailAnalysis.setRiskLevel(detectRiskLevel(normalizedText));

        if (request.getApplicationId() != null) {
            JobApplication application = jobApplicationRepository.findByOwnerOrderByUpdatedAtDesc(currentUser).stream()
                    .filter(item -> item.getId().equals(request.getApplicationId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
            emailAnalysis.setApplication(application);
        }

        return trackerMapper.toEmailResponse(emailAnalysisRepository.save(emailAnalysis));
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    public void delete(Long id) {
        EmailAnalysis email = emailAnalysisRepository.findAllByOwnerOrderByCreatedAtDesc(currentUserService.getCurrentUser()).stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Email analysis not found"));
        emailAnalysisRepository.delete(email);
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

    private boolean isActionRequired(String text) {
        return containsAny(text, "reply", "confirm", "availability", "deadline", "submit", "complete", "send", "tomorrow", "today");
    }

    private String detectIntent(String text) {
        if (containsAny(text, "availability", "schedule", "calendar", "interview")) {
            return "Schedule interview";
        }
        if (containsAny(text, "assignment", "assessment", "challenge", "take-home")) {
            return "Complete assessment";
        }
        if (containsAny(text, "offer", "compensation", "joining", "benefits")) {
            return "Review offer";
        }
        if (containsAny(text, "unfortunately", "regret", "moved forward")) {
            return "Close loop";
        }
        if (containsAny(text, "documents", "transcript", "resume", "portfolio")) {
            return "Share documents";
        }
        return "General update";
    }

    private String detectResponseWindow(String text) {
        if (containsAny(text, "today", "asap", "immediately")) {
            return "Reply today";
        }
        if (containsAny(text, "tomorrow", "within 24 hours")) {
            return "Reply within 24 hours";
        }
        if (containsAny(text, "this week", "within 3 days", "deadline")) {
            return "Reply this week";
        }
        return "No explicit deadline";
    }

    private String generateNextStep(String text) {
        if (containsAny(text, "availability", "schedule", "interview")) {
            return "Send 3-4 available interview slots and mention your timezone.";
        }
        if (containsAny(text, "assignment", "assessment", "challenge")) {
            return "Plan the assessment block, confirm the deadline, and prepare submission notes.";
        }
        if (containsAny(text, "offer", "compensation")) {
            return "Review compensation, timeline, and expectations before replying.";
        }
        if (containsAny(text, "documents", "resume", "transcript", "portfolio")) {
            return "Send the requested documents in one reply and restate your interest clearly.";
        }
        if (containsAny(text, "unfortunately", "regret")) {
            return "Record the outcome and move attention to active applications.";
        }
        return "Capture the update and decide whether a response or follow-up is needed.";
    }

    private String generateReplyDraft(String subject, String text) {
        if (containsAny(text, "availability", "interview", "schedule")) {
            return "Hi, thank you for the update regarding " + subject + ". I am available for the interview and can share suitable time slots today. Please let me know the preferred time zone and format for the round.";
        }
        if (containsAny(text, "assignment", "assessment", "challenge")) {
            return "Hi, thank you for sharing the assessment details. I have reviewed the instructions and will complete the task within the requested timeline. Please let me know if there is any preferred submission format or evaluation criteria I should keep in mind.";
        }
        if (containsAny(text, "documents", "resume", "transcript", "portfolio")) {
            return "Hi, thank you for the update. I have attached the requested documents. Please let me know if you need anything else from my side.";
        }
        if (containsAny(text, "offer", "compensation")) {
            return "Hi, thank you for sharing the offer details. I am reviewing the information carefully and will get back to you shortly with my response.";
        }
        return "Hi, thank you for the update. I have noted the details and will follow up as needed. Please let me know if you need anything else from my side.";
    }

    private String detectRiskLevel(String text) {
        if (containsAny(text, "today", "tomorrow", "deadline", "asap", "immediately")) {
            return "High";
        }
        if (containsAny(text, "assessment", "assignment", "documents", "availability")) {
            return "Medium";
        }
        return "Low";
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
