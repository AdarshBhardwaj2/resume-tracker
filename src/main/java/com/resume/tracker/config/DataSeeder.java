package com.resume.tracker.config;

import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.model.EmailAnalysis;
import com.resume.tracker.model.JobApplication;
import com.resume.tracker.model.PriorityLevel;
import com.resume.tracker.repository.EmailAnalysisRepository;
import com.resume.tracker.repository.JobApplicationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final JobApplicationRepository jobApplicationRepository;
    private final EmailAnalysisRepository emailAnalysisRepository;

    public DataSeeder(JobApplicationRepository jobApplicationRepository,
                      EmailAnalysisRepository emailAnalysisRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.emailAnalysisRepository = emailAnalysisRepository;
    }

    @Override
    public void run(String... args) {
        if (jobApplicationRepository.count() > 0 || emailAnalysisRepository.count() > 0) {
            return;
        }

        JobApplication applicationOne = new JobApplication();
        applicationOne.setCompany("Atlassian");
        applicationOne.setRole("Software Engineer Intern");
        applicationOne.setLocation("Remote");
        applicationOne.setSource("LinkedIn");
        applicationOne.setStatus(ApplicationStatus.INTERVIEW);
        applicationOne.setPriority(PriorityLevel.HIGH);
        applicationOne.setAppliedDate(LocalDate.now().minusDays(6));
        applicationOne.setFollowUpDate(LocalDate.now().plusDays(2));
        applicationOne.setCompensation("18 LPA");
        applicationOne.setTechStack("Java, Spring Boot, React, PostgreSQL");
        applicationOne.setTags("backend,internship,remote");
        applicationOne.setNotes("Hiring manager appreciated the resume's backend project section.");

        JobApplication applicationTwo = new JobApplication();
        applicationTwo.setCompany("Razorpay");
        applicationTwo.setRole("SDE-1");
        applicationTwo.setLocation("Bangalore");
        applicationTwo.setSource("Referral");
        applicationTwo.setStatus(ApplicationStatus.APPLIED);
        applicationTwo.setPriority(PriorityLevel.MEDIUM);
        applicationTwo.setAppliedDate(LocalDate.now().minusDays(3));
        applicationTwo.setFollowUpDate(LocalDate.now().plusDays(5));
        applicationTwo.setCompensation("22 LPA");
        applicationTwo.setTechStack("Spring Boot, Redis, AWS");
        applicationTwo.setTags("payments,backend,referral");
        applicationTwo.setNotes("Need to send updated transcript if shortlisted.");

        jobApplicationRepository.saveAll(List.of(applicationOne, applicationTwo));

        EmailAnalysis emailAnalysis = new EmailAnalysis();
        emailAnalysis.setSender("recruiter@atlassian.com");
        emailAnalysis.setSubject("Interview round confirmation");
        emailAnalysis.setBody("We are happy to invite you to the next interview round. Please confirm your availability by tomorrow.");
        emailAnalysis.setCategory("Interview");
        emailAnalysis.setDetectedStage(ApplicationStatus.INTERVIEW);
        emailAnalysis.setUrgency("High");
        emailAnalysis.setTone("Positive");
        emailAnalysis.setConfidenceScore(88);
        emailAnalysis.setSummary("Recruiter confirmed the next round and expects a quick response.");
        emailAnalysis.setSuggestedAction("Reply with interview availability and prepare system design talking points.");
        emailAnalysis.setActionRequired(true);
        emailAnalysis.setRecruiterIntent("Schedule interview");
        emailAnalysis.setResponseWindow("Reply within 24 hours");
        emailAnalysis.setNextStep("Send your available interview slots and confirm your timezone.");
        emailAnalysis.setReplyDraft("Hi, thank you for the update. I am available for the next round and can share suitable time slots today. Please let me know the preferred time zone for scheduling.");
        emailAnalysis.setRiskLevel("High");
        emailAnalysis.setApplication(applicationOne);

        emailAnalysisRepository.save(emailAnalysis);
    }
}
