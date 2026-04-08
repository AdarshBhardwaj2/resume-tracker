CREATE DATABASE resume_tracker;

\c resume_tracker;

CREATE TABLE IF NOT EXISTS job_applications (
    id BIGSERIAL PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    source VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    applied_date DATE NOT NULL,
    follow_up_date DATE,
    compensation VARCHAR(255),
    tech_stack VARCHAR(800),
    tags VARCHAR(500),
    notes VARCHAR(4000),
    resume_file_name VARCHAR(255),
    resume_stored_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS email_analysis (
    id BIGSERIAL PRIMARY KEY,
    sender VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body VARCHAR(6000) NOT NULL,
    category VARCHAR(255) NOT NULL,
    detected_stage VARCHAR(50) NOT NULL,
    urgency VARCHAR(100) NOT NULL,
    tone VARCHAR(100) NOT NULL,
    confidence_score INTEGER NOT NULL,
    summary VARCHAR(2000) NOT NULL,
    suggested_action VARCHAR(2000) NOT NULL,
    application_id BIGINT REFERENCES job_applications(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL
);
