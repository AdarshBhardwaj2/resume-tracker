package com.resume.tracker.service;

import com.resume.tracker.dto.InterviewPrepResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class InterviewPrepService {

    public InterviewPrepResponse getPrep(String role) {
        String normalizedRole = role.toLowerCase(Locale.ENGLISH).trim();

        if (normalizedRole.contains("frontend")) {
            return buildFrontendPrep();
        }
        if (normalizedRole.contains("full")) {
            return buildFullStackPrep();
        }
        if (normalizedRole.contains("data")) {
            return buildDataPrep();
        }
        return buildBackendPrep();
    }

    private InterviewPrepResponse buildBackendPrep() {
        InterviewPrepResponse response = new InterviewPrepResponse();
        response.setRole("Backend Developer");
        response.setFocusSummary("Focus on Java, Spring Boot, APIs, database design, caching, and project explanation.");
        response.setCoreTopics(List.of(
                "Java basics, OOP, collections, streams",
                "Spring Boot controllers, services, JPA, validation",
                "REST API design and HTTP status handling",
                "SQL joins, indexes, normalization, PostgreSQL basics",
                "Caching, Docker, and deployment basics"
        ));
        response.setTopicDetails(List.of(
                "Be ready to explain why you used layered architecture and how controllers, services, and repositories interact.",
                "Revise status codes, request validation, DTO mapping, and how you would secure an API in a real project.",
                "Know how your database schema works, why fields were chosen, and how follow-up dates or filters are queried.",
                "Understand when caching helps and when stale data can become a problem.",
                "Explain deployment clearly: frontend, backend, database, environment variables, and hosted storage."
        ));
        response.setResumeChecklist(List.of(
                "Highlight backend projects with clear impact and numbers",
                "Mention APIs, database design, authentication, and deployment",
                "Show Java and Spring Boot before general tools",
                "Keep project bullets outcome-focused, not only feature lists"
        ));
        response.setLikelyQuestions(List.of(
                "Explain one backend project end to end",
                "How does Spring Boot handle dependency injection?",
                "How would you design a REST API for job applications?",
                "What is the difference between SQL joins and when do you use indexes?",
                "When would you use caching in a project?"
        ));
        response.setProjectTalkingPoints(List.of(
                "Start with the problem the project solves, then explain the main modules in order.",
                "Mention one technical challenge such as file upload, filtering, or API design and how you handled it.",
                "Show one decision you made for simplicity and one improvement you would add in the future."
        ));
        response.setFinalRoundTips(List.of(
                "Practice a 90-second summary of your strongest project.",
                "Keep one example ready for teamwork, deadlines, and debugging.",
                "If asked about scaling, mention caching, indexing, and moving file storage to cloud services."
        ));
        response.setResources(List.of(
                new InterviewPrepResponse.PrepResource("Spring Boot Docs", "Core concepts, configuration, MVC, data, and deployment.", "https://docs.spring.io/spring-boot/documentation.html"),
                new InterviewPrepResponse.PrepResource("PostgreSQL Docs", "Schema design, queries, indexing, and performance basics.", "https://www.postgresql.org/docs/"),
                new InterviewPrepResponse.PrepResource("Docker Get Started", "Good for explaining container basics and deployment flow.", "https://docs.docker.com/get-started/"),
                new InterviewPrepResponse.PrepResource("React Learn", "Useful if your project discussion includes frontend integration.", "https://react.dev/learn")
        ));
        return response;
    }

    private InterviewPrepResponse buildFrontendPrep() {
        InterviewPrepResponse response = new InterviewPrepResponse();
        response.setRole("Frontend Developer");
        response.setFocusSummary("Focus on React fundamentals, component structure, state, forms, rendering, and UI decisions.");
        response.setCoreTopics(List.of(
                "HTML, CSS, responsive layout, spacing, typography",
                "React components, props, state, effects",
                "Form handling and API integration",
                "Conditional rendering and list rendering",
                "Performance basics and clean component structure"
        ));
        response.setTopicDetails(List.of(
                "Be able to explain why the current layout feels clean, readable, and more professional than a default dashboard.",
                "Revise how state updates trigger re-renders and how forms are managed in React.",
                "Know how API data is fetched, rendered, and reused in separate sections of the interface.",
                "Prepare examples of UI improvements you made and why they improved clarity.",
                "Be ready to discuss responsive behavior and how the layout changes on smaller screens."
        ));
        response.setResumeChecklist(List.of(
                "Show polished UI projects, not only functionality",
                "Mention React, Vite, API integration, and responsive design",
                "Include a project where you improved usability or design quality",
                "Keep screenshots or deployed links ready for discussion"
        ));
        response.setLikelyQuestions(List.of(
                "How do props and state differ in React?",
                "How do you structure a large frontend project?",
                "How do you handle forms and API calls?",
                "How do you make a UI responsive and accessible?",
                "How would you improve a cluttered interface?"
        ));
        response.setProjectTalkingPoints(List.of(
                "Explain the design direction in simple terms: cleaner spacing, fewer distractions, and clearer sections.",
                "Show how you grouped similar features into tabs to reduce clutter.",
                "Mention how data-heavy screens were simplified using cards, tables, and lightweight action areas."
        ));
        response.setFinalRoundTips(List.of(
                "Keep one explanation ready for design tradeoffs: minimal look versus feature density.",
                "Be able to explain how you improved an earlier rough UI into the current version.",
                "If asked about next steps, mention component reuse, charts, and accessibility improvements."
        ));
        response.setResources(List.of(
                new InterviewPrepResponse.PrepResource("React Learn", "Official React learning path for components, state, and rendering.", "https://react.dev/learn"),
                new InterviewPrepResponse.PrepResource("Thinking in React", "Helpful for discussing how you structured the UI.", "https://react.dev/learn/thinking-in-react"),
                new InterviewPrepResponse.PrepResource("MDN JavaScript Guide", "Good revision source for core JavaScript topics.", "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide")
        ));
        return response;
    }

    private InterviewPrepResponse buildFullStackPrep() {
        InterviewPrepResponse response = new InterviewPrepResponse();
        response.setRole("Full Stack Developer");
        response.setFocusSummary("Be ready to explain frontend, backend, database flow, deployment, and tradeoffs in one project.");
        response.setCoreTopics(List.of(
                "React app structure and API integration",
                "Spring Boot backend layers and validation",
                "PostgreSQL schema design and queries",
                "Authentication, deployment, and environment variables",
                "How data moves through the entire application"
        ));
        response.setTopicDetails(List.of(
                "Prepare to explain the complete flow from browser input to database storage and back to UI rendering.",
                "Revise how environment variables differ between local development and deployment.",
                "Know how your app separates frontend and backend responsibilities.",
                "Be able to explain why hosting choices matter for a full stack app.",
                "Show how one feature moves through multiple layers, for example resume match or email analysis."
        ));
        response.setResumeChecklist(List.of(
                "Show complete projects with frontend, backend, and database",
                "Mention deployment and hosting, not just localhost setup",
                "Explain architecture decisions briefly and clearly",
                "Include impact, scale, or complexity in project bullets"
        ));
        response.setLikelyQuestions(List.of(
                "Walk through your project from UI to database",
                "How do frontend and backend communicate?",
                "How did you structure the database tables?",
                "How would you deploy the project publicly?",
                "What would you improve in the next version?"
        ));
        response.setProjectTalkingPoints(List.of(
                "Start with the user flow, then explain the technical flow.",
                "Mention one frontend improvement, one backend feature, and one database decision.",
                "Show how the project can be extended with authentication, cloud storage, and hosted deployment."
        ));
        response.setFinalRoundTips(List.of(
                "Keep a simple architecture explanation ready without too much jargon.",
                "Be honest about current limitations and what you would improve next.",
                "If asked about production readiness, mention hosted DB, CORS, env vars, and cloud file storage."
        ));
        response.setResources(List.of(
                new InterviewPrepResponse.PrepResource("React Learn", "Frontend structure, components, state, and rendering.", "https://react.dev/learn"),
                new InterviewPrepResponse.PrepResource("Spring Boot Docs", "Backend architecture, config, and deployment basics.", "https://docs.spring.io/spring-boot/documentation.html"),
                new InterviewPrepResponse.PrepResource("PostgreSQL Docs", "Schema design, queries, and database concepts.", "https://www.postgresql.org/docs/"),
                new InterviewPrepResponse.PrepResource("Docker Get Started", "Good support material for deployment discussions.", "https://docs.docker.com/get-started/")
        ));
        return response;
    }

    private InterviewPrepResponse buildDataPrep() {
        InterviewPrepResponse response = new InterviewPrepResponse();
        response.setRole("Data Analyst / Data Engineer");
        response.setFocusSummary("Prepare SQL, data modeling, data cleaning, reporting, and problem-solving explanations.");
        response.setCoreTopics(List.of(
                "SQL joins, grouping, aggregation, window functions",
                "Data cleaning and transformation",
                "Basic Python workflow for data tasks",
                "Database design and query performance",
                "Explaining insights clearly to non-technical users"
        ));
        response.setTopicDetails(List.of(
                "Revise how you approach raw data, missing values, and transformation steps.",
                "Be able to compare different SQL operations with clear examples.",
                "Prepare one case where analysis or reporting leads to a decision.",
                "Know how to talk about performance in terms of indexing and query structure.",
                "Practice explaining technical work in simple business language."
        ));
        response.setResumeChecklist(List.of(
                "Highlight SQL, dashboards, reporting, and data projects",
                "Show measurable outcomes from analysis work",
                "Mention data cleaning, ETL, or reporting tools if used",
                "Keep project explanations simple and result-focused"
        ));
        response.setLikelyQuestions(List.of(
                "How do you approach a messy dataset?",
                "What is the difference between inner join and left join?",
                "How do you optimize a slow SQL query?",
                "How do you explain findings to stakeholders?",
                "Describe one project where your analysis changed a decision"
        ));
        response.setProjectTalkingPoints(List.of(
                "Explain the dataset, your process, the tools used, and the final output.",
                "Mention how you cleaned data before analysis.",
                "Show one meaningful insight and what action it supported."
        ));
        response.setFinalRoundTips(List.of(
                "Use simple examples when explaining joins and aggregation.",
                "Keep one end-to-end project explanation ready with business impact.",
                "If asked about growth, mention stronger Python, ETL, and dashboard tooling."
        ));
        response.setResources(List.of(
                new InterviewPrepResponse.PrepResource("PostgreSQL Docs", "Useful for SQL revision and database concepts.", "https://www.postgresql.org/docs/"),
                new InterviewPrepResponse.PrepResource("MDN JavaScript Guide", "Helpful if your data work includes basic web dashboards.", "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide")
        ));
        return response;
    }
}
