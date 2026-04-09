package com.resume.tracker.service;

import com.resume.tracker.dto.DashboardStatsResponse;
import com.resume.tracker.mapper.TrackerMapper;
import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.repository.EmailAnalysisRepository;
import com.resume.tracker.repository.JobApplicationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final JobApplicationRepository jobApplicationRepository;
    private final EmailAnalysisRepository emailAnalysisRepository;
    private final TrackerMapper trackerMapper;

    public DashboardService(JobApplicationRepository jobApplicationRepository,
                            EmailAnalysisRepository emailAnalysisRepository,
                            TrackerMapper trackerMapper) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.emailAnalysisRepository = emailAnalysisRepository;
        this.trackerMapper = trackerMapper;
    }

    @Cacheable("dashboardStats")
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        List<com.resume.tracker.model.JobApplication> applications = jobApplicationRepository.findAll();
        long total = jobApplicationRepository.count();
        long active = jobApplicationRepository.countByStatusIn(List.of(
                ApplicationStatus.APPLIED,
                ApplicationStatus.SHORTLISTED,
                ApplicationStatus.INTERVIEW
        ));
        long interviews = jobApplicationRepository.countByStatus(ApplicationStatus.INTERVIEW);
        long followUps = jobApplicationRepository.countByFollowUpDateLessThanEqual(LocalDate.now().plusDays(2));
        long offers = jobApplicationRepository.countByStatus(ApplicationStatus.OFFER);
        long rejections = jobApplicationRepository.countByStatus(ApplicationStatus.REJECTED);
        long thisWeek = applications.stream()
                .filter(application -> !application.getAppliedDate().isBefore(LocalDate.now().minusDays(7)))
                .count();
        long stale = applications.stream()
                .filter(application -> List.of(ApplicationStatus.APPLIED, ApplicationStatus.SHORTLISTED).contains(application.getStatus()))
                .filter(application -> application.getUpdatedAt().toLocalDate().isBefore(LocalDate.now().minusDays(10)))
                .count();

        response.setTotalApplications(total);
        response.setActivePipelines(active);
        response.setInterviewsScheduled(interviews);
        response.setFollowUpsDue(followUps);
        response.setOffersReceived(offers);
        response.setRejectionCount(rejections);
        response.setApplicationsThisWeek(thisWeek);
        response.setStaleApplications(stale);
        response.setResponseRate(total == 0 ? 0.0 : Math.round(((double) active / total) * 1000.0) / 10.0);
        response.setInterviewRate(total == 0 ? 0.0 : Math.round(((double) interviews / total) * 1000.0) / 10.0);
        response.setTopCompanies(applications.stream()
                .collect(Collectors.groupingBy(application -> application.getCompany().trim(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList());
        response.setFocusAreas(buildFocusAreas(total, followUps, stale, offers, interviews, thisWeek));
        response.setStatusBreakdown(enumBreakdown());
        response.setPriorityBreakdown(applications.stream()
                .collect(Collectors.groupingBy(application -> application.getPriority().name(), LinkedHashMap::new, Collectors.counting())));
        response.setRecentApplications(jobApplicationRepository.findTop5ByOrderByUpdatedAtDesc()
                .stream()
                .map(trackerMapper::toApplicationResponse)
                .toList());
        response.setRecentEmailInsights(emailAnalysisRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(trackerMapper::toEmailResponse)
                .toList());
        return response;
    }

    private List<String> buildFocusAreas(long total, long followUps, long stale, long offers, long interviews, long thisWeek) {
        List<String> focusAreas = new ArrayList<>();
        if (total == 0) {
            focusAreas.add("Start by adding 5 target roles so the tracker can generate useful pipeline insights.");
        }
        if (followUps > 0) {
            focusAreas.add("You have follow-ups due in the next 48 hours. Prioritize recruiter replies and interview confirmations.");
        }
        if (stale > 0) {
            focusAreas.add("Some active applications have been quiet for more than 10 days. Mark them for follow-up or archive them.");
        }
        if (offers > 0) {
            focusAreas.add("At least one offer is in progress. Compare compensation, growth, and role fit before responding.");
        }
        if (interviews == 0 && total > 3) {
            focusAreas.add("Your application volume is healthy, but interview conversion is still low. Tailor resumes more aggressively.");
        }
        if (thisWeek < 3 && total > 0) {
            focusAreas.add("Pipeline momentum is slowing this week. Add a few new applications to keep the funnel active.");
        }
        if (focusAreas.isEmpty()) {
            focusAreas.add("Pipeline health looks balanced. Keep capturing every recruiter email so the dashboard stays accurate.");
        }
        return focusAreas;
    }

    private Map<String, Long> enumBreakdown() {
        Map<String, Long> breakdown = new LinkedHashMap<>();
        Arrays.stream(ApplicationStatus.values()).forEach(status -> breakdown.put(
                status.name(),
                jobApplicationRepository.countByStatus(status)
        ));
        return breakdown;
    }
}
