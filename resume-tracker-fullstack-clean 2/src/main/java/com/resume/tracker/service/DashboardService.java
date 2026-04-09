package com.resume.tracker.service;

import com.resume.tracker.dto.DashboardStatsResponse;
import com.resume.tracker.mapper.TrackerMapper;
import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.repository.EmailAnalysisRepository;
import com.resume.tracker.repository.JobApplicationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        long total = jobApplicationRepository.count();
        long active = jobApplicationRepository.countByStatusIn(List.of(
                ApplicationStatus.APPLIED,
                ApplicationStatus.SHORTLISTED,
                ApplicationStatus.INTERVIEW
        ));
        long interviews = jobApplicationRepository.countByStatus(ApplicationStatus.INTERVIEW);
        long followUps = jobApplicationRepository.countByFollowUpDateLessThanEqual(LocalDate.now().plusDays(2));

        response.setTotalApplications(total);
        response.setActivePipelines(active);
        response.setInterviewsScheduled(interviews);
        response.setFollowUpsDue(followUps);
        response.setResponseRate(total == 0 ? 0.0 : Math.round(((double) active / total) * 1000.0) / 10.0);
        response.setTopCompanies(jobApplicationRepository.findAll().stream()
                .collect(Collectors.groupingBy(application -> application.getCompany().trim(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList());
        response.setStatusBreakdown(enumBreakdown());
        response.setPriorityBreakdown(jobApplicationRepository.findAll().stream()
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

    private Map<String, Long> enumBreakdown() {
        Map<String, Long> breakdown = new LinkedHashMap<>();
        Arrays.stream(ApplicationStatus.values()).forEach(status -> breakdown.put(
                status.name(),
                jobApplicationRepository.countByStatus(status)
        ));
        return breakdown;
    }
}
