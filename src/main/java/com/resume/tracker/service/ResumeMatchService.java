package com.resume.tracker.service;

import com.resume.tracker.dto.ResumeMatchRequest;
import com.resume.tracker.dto.ResumeMatchResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeMatchService {

    private static final List<String> KNOWN_SKILLS = List.of(
            "java", "spring boot", "spring", "react", "javascript", "typescript", "postgresql", "mysql",
            "redis", "docker", "kubernetes", "aws", "gcp", "rest api", "microservices", "hibernate",
            "jpa", "sql", "html", "css", "node.js", "git", "github", "testing", "junit", "ci/cd",
            "linux", "data structures", "algorithms", "system design", "mongodb", "python", "c++"
    );

    private static final List<String> ATS_SECTIONS = List.of(
            "summary", "experience", "projects", "skills", "education", "internship", "achievements"
    );

    public ResumeMatchResponse analyze(ResumeMatchRequest request) {
        String resume = normalize(request.getResumeText());
        String jobDescription = normalize(request.getJobDescription());
        String targetRole = normalize(request.getTargetRole());

        List<String> requiredKeywords = extractKeywords(jobDescription, targetRole);
        List<String> matchedKeywords = requiredKeywords.stream()
                .filter(resume::contains)
                .distinct()
                .sorted()
                .toList();
        List<String> missingKeywords = requiredKeywords.stream()
                .filter(keyword -> !resume.contains(keyword))
                .distinct()
                .sorted()
                .limit(10)
                .toList();

        int skillsScore = scoreSkills(requiredKeywords, matchedKeywords);
        int atsScore = scoreAtsReadiness(resume);
        int roleAlignment = scoreRoleAlignment(resume, targetRole, jobDescription, matchedKeywords);
        int overall = Math.round((skillsScore * 0.5f) + (atsScore * 0.2f) + (roleAlignment * 0.3f));

        ResumeMatchResponse response = new ResumeMatchResponse();
        response.setSkillsScore(skillsScore);
        response.setAtsReadinessScore(atsScore);
        response.setRoleAlignmentScore(roleAlignment);
        response.setOverallScore(Math.min(overall, 98));
        response.setFitLabel(fitLabel(overall));
        response.setMatchedKeywords(matchedKeywords);
        response.setMissingKeywords(missingKeywords);
        response.setStrengths(buildStrengths(resume, matchedKeywords, atsScore, roleAlignment));
        response.setRecommendations(buildRecommendations(resume, targetRole, missingKeywords, atsScore, roleAlignment));
        response.setSummary(buildSummary(request.getTargetRole(), overall, matchedKeywords.size(), missingKeywords.size()));
        return response;
    }

    private List<String> extractKeywords(String jobDescription, String targetRole) {
        Set<String> keywords = new LinkedHashSet<>();
        KNOWN_SKILLS.stream()
                .filter(jobDescription::contains)
                .forEach(keywords::add);

        tokenize(jobDescription).stream()
                .filter(token -> token.length() > 4)
                .filter(token -> !isCommonWord(token))
                .sorted(Comparator.comparingInt(String::length).reversed())
                .limit(12)
                .forEach(keywords::add);

        tokenize(targetRole).stream()
                .filter(token -> token.length() > 2)
                .forEach(keywords::add);

        return keywords.stream().limit(15).toList();
    }

    private int scoreSkills(List<String> requiredKeywords, List<String> matchedKeywords) {
        if (requiredKeywords.isEmpty()) {
            return 65;
        }
        double ratio = (double) matchedKeywords.size() / requiredKeywords.size();
        return (int) Math.round(45 + (ratio * 50));
    }

    private int scoreAtsReadiness(String resume) {
        int score = 52;

        for (String section : ATS_SECTIONS) {
            if (resume.contains(section)) {
                score += 5;
            }
        }

        if (Pattern.compile("\\b\\d+%|\\b\\d+x|\\b\\d+\\+").matcher(resume).find()) {
            score += 8;
        }
        if (containsAny(resume, "built", "developed", "designed", "implemented", "optimized", "delivered")) {
            score += 8;
        }
        if (resume.length() > 800) {
            score += 6;
        }

        return Math.min(score, 95);
    }

    private int scoreRoleAlignment(String resume, String targetRole, String jobDescription, List<String> matchedKeywords) {
        int score = 44;

        if (containsAllRoleWords(resume, targetRole)) {
            score += 18;
        }
        if (containsAny(resume, "project", "internship", "experience")) {
            score += 10;
        }
        if (containsAny(jobDescription, "backend", "frontend", "full stack", "software engineer", "developer")
                && containsAny(resume, "backend", "frontend", "full stack", "software engineer", "developer")) {
            score += 12;
        }
        score += Math.min(matchedKeywords.size() * 2, 18);

        return Math.min(score, 96);
    }

    private List<String> buildStrengths(String resume, List<String> matchedKeywords, int atsScore, int roleAlignment) {
        List<String> strengths = new ArrayList<>();
        if (matchedKeywords.size() >= 5) {
            strengths.add("The resume already overlaps well with the job description on core technical keywords.");
        }
        if (atsScore >= 75) {
            strengths.add("The resume text appears reasonably ATS-friendly with recognizable sections and action-oriented phrasing.");
        }
        if (roleAlignment >= 75) {
            strengths.add("The resume aligns clearly with the target role instead of reading like a generic student profile.");
        }
        if (Pattern.compile("\\b\\d+%|\\b\\d+x|\\b\\d+\\+").matcher(resume).find()) {
            strengths.add("Quantified impact is present, which improves recruiter trust and shortlist potential.");
        }
        if (strengths.isEmpty()) {
            strengths.add("The foundation is workable, but the resume needs stronger targeting for this role.");
        }
        return strengths;
    }

    private List<String> buildRecommendations(String resume,
                                              String targetRole,
                                              List<String> missingKeywords,
                                              int atsScore,
                                              int roleAlignment) {
        List<String> recommendations = new ArrayList<>();

        if (!missingKeywords.isEmpty()) {
            recommendations.add("Add role-relevant keywords where truthful, especially: " + String.join(", ", missingKeywords.stream().limit(6).toList()) + ".");
        }
        if (!Pattern.compile("\\b\\d+%|\\b\\d+x|\\b\\d+\\+").matcher(resume).find()) {
            recommendations.add("Rewrite project bullets with measurable impact such as latency reduction, scale, accuracy, or delivery speed.");
        }
        if (!containsAny(resume, "projects", "project")) {
            recommendations.add("Include a dedicated projects section that proves hands-on work relevant to " + targetRole + ".");
        }
        if (!containsAny(resume, "skills")) {
            recommendations.add("Add a compact technical skills section so ATS systems can parse your stack more reliably.");
        }
        if (atsScore < 72) {
            recommendations.add("Use clearer section headings like Summary, Skills, Projects, Experience, and Education for ATS readability.");
        }
        if (roleAlignment < 70) {
            recommendations.add("Tailor the summary and top 2 projects directly to the target role instead of using broad academic wording.");
        }
        return recommendations.stream().limit(5).toList();
    }

    private String buildSummary(String targetRole, int overall, int matchedCount, int missingCount) {
        return "For the role of " + targetRole + ", the resume scores " + overall
                + "/100 with " + matchedCount + " strong keyword matches and "
                + missingCount + " notable gaps to address.";
    }

    private String fitLabel(int overall) {
        if (overall >= 82) {
            return "Strong Fit";
        }
        if (overall >= 68) {
            return "Moderate Fit";
        }
        return "Needs Tailoring";
    }

    private boolean containsAllRoleWords(String resume, String targetRole) {
        List<String> words = tokenize(targetRole).stream()
                .filter(token -> token.length() > 2)
                .toList();
        return !words.isEmpty() && words.stream().allMatch(resume::contains);
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }
        return false;
    }

    private List<String> tokenize(String text) {
        Matcher matcher = Pattern.compile("[a-zA-Z][a-zA-Z+.#/-]{2,}").matcher(text);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group().toLowerCase(Locale.ENGLISH));
        }
        return tokens;
    }

    private boolean isCommonWord(String token) {
        return Set.of(
                "their", "there", "about", "should", "would", "could", "candidate", "looking", "across",
                "strong", "skills", "experience", "project", "projects", "team", "teams", "years",
                "ability", "using", "build", "built", "develop", "developed", "design", "system",
                "work", "working", "role", "responsible", "preferred", "required"
        ).contains(token);
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ENGLISH).replace("\r", " ").replace("\n", " ");
    }
}
