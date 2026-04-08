package com.resume.tracker.repository;

import com.resume.tracker.model.EmailAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailAnalysisRepository extends JpaRepository<EmailAnalysis, Long> {

    List<EmailAnalysis> findTop5ByOrderByCreatedAtDesc();
}
