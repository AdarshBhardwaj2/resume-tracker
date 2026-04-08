package com.resume.tracker.service;

import com.resume.tracker.dto.ApplicationRequest;
import com.resume.tracker.dto.ApplicationResponse;
import com.resume.tracker.exception.ResourceNotFoundException;
import com.resume.tracker.mapper.TrackerMapper;
import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.model.JobApplication;
import com.resume.tracker.repository.JobApplicationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ResumeStorageService resumeStorageService;
    private final TrackerMapper trackerMapper;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository,
                                 ResumeStorageService resumeStorageService,
                                 TrackerMapper trackerMapper) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.resumeStorageService = resumeStorageService;
        this.trackerMapper = trackerMapper;
    }

    public List<ApplicationResponse> getAll(String status, String keyword) {
        List<JobApplication> applications;

        if (keyword != null && !keyword.isBlank()) {
            applications = jobApplicationRepository.findByCompanyContainingIgnoreCaseOrRoleContainingIgnoreCase(keyword, keyword);
        } else if (status != null && !status.isBlank()) {
            applications = jobApplicationRepository.findByStatus(ApplicationStatus.valueOf(status.toUpperCase()));
        } else {
            applications = jobApplicationRepository.findAll();
        }

        return applications.stream()
                .sorted(Comparator.comparing(JobApplication::getUpdatedAt).reversed())
                .map(trackerMapper::toApplicationResponse)
                .toList();
    }

    public ApplicationResponse getOne(Long id) {
        return trackerMapper.toApplicationResponse(fetchById(id));
    }

    @CacheEvict(value = {"dashboardStats", "applicationMetrics"}, allEntries = true)
    public ApplicationResponse create(ApplicationRequest request, MultipartFile resume) {
        JobApplication application = new JobApplication();
        applyRequest(application, request);
        updateResume(application, resume);
        return trackerMapper.toApplicationResponse(jobApplicationRepository.save(application));
    }

    @CacheEvict(value = {"dashboardStats", "applicationMetrics"}, allEntries = true)
    public ApplicationResponse update(Long id, ApplicationRequest request, MultipartFile resume) {
        JobApplication application = fetchById(id);
        applyRequest(application, request);
        updateResume(application, resume);
        return trackerMapper.toApplicationResponse(jobApplicationRepository.save(application));
    }

    @CacheEvict(value = {"dashboardStats", "applicationMetrics"}, allEntries = true)
    public void delete(Long id) {
        JobApplication application = fetchById(id);
        resumeStorageService.deleteIfPresent(application.getResumeStoredName());
        jobApplicationRepository.delete(application);
    }

    private JobApplication fetchById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private void applyRequest(JobApplication application, ApplicationRequest request) {
        application.setCompany(request.getCompany());
        application.setRole(request.getRole());
        application.setLocation(request.getLocation());
        application.setSource(request.getSource());
        application.setStatus(request.getStatus());
        application.setPriority(request.getPriority());
        application.setAppliedDate(request.getAppliedDate());
        application.setFollowUpDate(request.getFollowUpDate());
        application.setCompensation(request.getCompensation());
        application.setTechStack(request.getTechStack());
        application.setTags(request.getTags());
        application.setNotes(request.getNotes());
    }

    private void updateResume(JobApplication application, MultipartFile resume) {
        if (resume == null || resume.isEmpty()) {
            return;
        }
        resumeStorageService.deleteIfPresent(application.getResumeStoredName());
        ResumeStorageService.StoredResume storedResume = resumeStorageService.store(resume);
        application.setResumeFileName(storedResume.originalFileName());
        application.setResumeStoredName(storedResume.storedFileName());
    }
}
