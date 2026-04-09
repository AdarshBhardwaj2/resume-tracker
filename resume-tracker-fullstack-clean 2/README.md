# Resume Tracker + Email Analyzer

A full-stack masters-level project using:

- Spring Boot 3
- React with Vite
- PostgreSQL
- Spring Data JPA
- Spring Cache
- Multipart resume upload

## Features

- Track job applications with company, role, source, status, priority, compensation, notes, tags, and follow-up date
- Upload and view resume files for each application
- Analyze recruiter emails and infer category, urgency, stage, tone, summary, and suggested action
- Link analyzed emails to applications
- Dashboard with status breakdown, priority breakdown, response rate, recent activity, and spotlight items
- In-memory caching for dashboard analytics
- Seed data for quick demo after first run

## Project Structure

```text
.
├── database
│   └── init.sql
├── frontend
│   ├── package.json
│   └── src
├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   └── test
└── pom.xml
```

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 22+
- PostgreSQL 14+

## Step-by-Step Run Guide

### 1. Create the database

Open PostgreSQL and run:

```sql
CREATE DATABASE resume_tracker;
```

If you want to use the SQL file:

```bash
psql -U postgres -f database/init.sql
```

### 2. Set database credentials

The backend reads these values:

```properties
DB_URL=jdbc:postgresql://localhost:5432/resume_tracker
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

You can either:

- keep the defaults in `src/main/resources/application.properties`, or
- override them in your terminal before starting the backend

macOS/Linux:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/resume_tracker
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

### 3. Start the Spring Boot backend

From the project root:

```bash
mvn spring-boot:run
```

Backend base URL:

```text
http://localhost:8080
```

### 4. Start the React frontend

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

### 5. Use the app

- Add job applications from the application form
- Upload a resume file while creating or updating an application
- Paste recruiter emails into the analyzer form
- Link an analyzed email to a selected application
- Open uploaded resume files from the application board

## API Summary

### Applications

- `GET /api/applications`
- `GET /api/applications?status=INTERVIEW`
- `GET /api/applications?keyword=backend`
- `POST /api/applications` multipart form-data
- `PUT /api/applications/{id}` multipart form-data
- `DELETE /api/applications/{id}`

### Emails

- `GET /api/emails`
- `POST /api/emails/analyze`
- `DELETE /api/emails/{id}`

### Dashboard

- `GET /api/dashboard/stats`

## Masters-Level Talking Points

You can present these as non-trivial additions in your viva or report:

- Layered backend design with DTOs, mapper, service layer, repository layer, and exception handling
- Resume file upload management with persistent metadata and static file access
- Rule-based email intelligence engine that converts unstructured email text into actionable job-pipeline insights
- Caching of dashboard analytics for reduced repeated aggregation cost
- Search and filtering support for pipeline management
- Clean separation between REST API and React frontend

## Build Commands

Backend jar:

```bash
mvn clean package
```

Frontend production build:

```bash
cd frontend
npm install
npm run build
```

## Notes

- Spring Boot uses `ddl-auto=update`, so it can create/update tables automatically.
- The `database/init.sql` file is included mainly for manual PostgreSQL setup and project submission.
- Uploaded resumes are stored in the `uploads` folder in the project root.
