package com.resume.tracker.repository;

import com.resume.tracker.model.ApplicationStatus;
import com.resume.tracker.model.JobApplication;
import com.resume.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByOwnerOrderByUpdatedAtDesc(User owner);

    List<JobApplication> findByOwnerAndStatus(User owner, ApplicationStatus status);

    long countByOwner(User owner);

    long countByOwnerAndStatusIn(User owner, List<ApplicationStatus> statuses);

    long countByOwnerAndStatus(User owner, ApplicationStatus status);

    long countByOwnerAndFollowUpDateLessThanEqual(User owner, LocalDate date);

    List<JobApplication> findTop5ByOwnerOrderByUpdatedAtDesc(User owner);

    List<JobApplication> findByOwnerIsNull();
}
