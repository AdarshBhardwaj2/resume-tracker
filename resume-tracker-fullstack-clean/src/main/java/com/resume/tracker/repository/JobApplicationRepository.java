package com.resume.tracker.repository;

import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByCompanyContainingIgnoreCaseOrRoleContainingIgnoreCase(String company, String role);

    long countByStatusIn(List<ApplicationStatus> statuses);

    long countByStatus(ApplicationStatus status);

    long countByFollowUpDateLessThanEqual(LocalDate date);

    List<JobApplication> findTop5ByOrderByUpdatedAtDesc();
}
