# Resume Tracker Pro

Resume Tracker Pro is a deployable full-stack application for managing job applications, storing resume versions, and analyzing recruiter emails. It is designed to look and behave more like a lightweight SaaS product than a basic CRUD assignment.

## Stack

- Frontend: React + Vite
- Backend: Spring Boot 3 + Spring Data JPA + Spring Cache
- Database: PostgreSQL
- File handling: multipart resume upload
- Deployment-ready: Docker, Vercel-friendly frontend env setup, Render/Railway-friendly backend config

## What Makes It Stronger Than a Basic CRUD App

- Pipeline analytics with cached dashboard metrics
- Application scoring for prioritization
- Follow-up queue and stale pipeline detection
- Rule-based recruiter email intelligence engine
- Kanban-style status board plus structured registry table
- Clean separation between React frontend and Spring Boot API
- Configurable CORS and environment-based hosting setup

## Main Features

- Add, update, delete, and search job applications
- Track company, role, source, compensation, follow-up date, tech stack, tags, and notes
- Upload resume files and open them from the dashboard
- Analyze recruiter emails into stage, tone, urgency, confidence, summary, and next action
- Compare a resume against a job description with fit scoring, keyword gaps, ATS readiness, and improvement suggestions
- Open role-based interview preparation notes with topics to revise, resume checklist items, likely questions, and useful links
- Link email insights to an application
- Dashboard metrics for response rate, interview rate, weekly application volume, offers, and stale applications
- Focus recommendations derived from pipeline state
- Minimal, product-style UI with overview, application workspace, email lab, and resume match lab

## Project Structure

```text
.
├── Dockerfile
├── docker-compose.yml
├── database
│   └── init.sql
├── frontend
│   ├── .env.example
│   ├── Dockerfile
│   ├── package.json
│   └── src
├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   └── test
└── pom.xml
```

## Use It Fully From VS Code

You can do everything from VS Code:

- open the project folder in VS Code
- use the integrated terminal for backend and frontend
- use the Explorer for code edits
- use the PostgreSQL extension or pgAdmin for database inspection
- use GitHub integration for deployment

Recommended terminal setup in VS Code:

- Terminal 1: Spring Boot backend
- Terminal 2: React frontend
- Terminal 3: optional Docker or Git commands

## Local Setup With pgAdmin

### 1. Create the database in pgAdmin

In pgAdmin:

1. connect to your PostgreSQL server
2. right click `Databases`
3. choose `Create -> Database`
4. enter `resume_tracker`
5. save

You do not need to manually create tables if you run the backend normally. Spring Boot will create and update them automatically.

## Local Run in VS Code

### 1. Backend environment

From the project root:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/resume_tracker
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export APP_CORS_ALLOWED_ORIGINS=http://localhost:5173
```

### 2. Start backend

```bash
cd /Users/adarsh/Documents/New\ project
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

### 3. Frontend environment

Copy the example file if you want:

```bash
cd /Users/adarsh/Documents/New\ project/frontend
cp .env.example .env
```

### 4. Start frontend

```bash
npm install
npm run dev
```

Frontend runs at:

```text
http://localhost:5173
```

## Alternative: Run With Docker

If you want one-command local startup:

```bash
docker compose up --build
```

This starts:

- PostgreSQL on `5432`
- Spring Boot backend on `8080`
- frontend on `4173`

Open:

```text
http://localhost:4173
```

## Hosting So Everyone Can Open It

The easiest public deployment path is:

- Frontend: Vercel
- Backend: Render or Railway
- Database: Neon PostgreSQL

### Recommended deployment architecture

- deploy the backend as a Spring Boot web service
- create a hosted PostgreSQL database on Neon
- set backend env vars in Render or Railway
- deploy the React frontend on Vercel
- set `VITE_API_BASE_URL` to your deployed backend URL
- set `VITE_FILE_BASE_URL` to the same backend URL
- set backend `APP_CORS_ALLOWED_ORIGINS` to your frontend domain

### Backend environment variables for hosting

```text
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<db-user>
DB_PASSWORD=<db-password>
APP_UPLOAD_DIR=uploads
APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app
```

### Frontend environment variables for hosting

```text
VITE_API_BASE_URL=https://your-backend-service.onrender.com
VITE_FILE_BASE_URL=https://your-backend-service.onrender.com
```

## API Endpoints

### Dashboard

- `GET /api/dashboard/stats`

### Applications

- `GET /api/applications`
- `GET /api/applications?status=INTERVIEW`
- `GET /api/applications?keyword=react`
- `POST /api/applications`
- `PUT /api/applications/{id}`
- `DELETE /api/applications/{id}`

### Email Analyzer

- `GET /api/emails`
- `POST /api/emails/analyze`
- `DELETE /api/emails/{id}`

### Resume Match

- `POST /api/resume-match`

### Interview Prep

- `GET /api/interview-prep?role=backend`

## Submission and Demo Talking Points

- Explain why dashboard stats are cached and where cache invalidation happens
- Show how recruiter emails are transformed into structured hiring signals
- Show how the UI supports both strategic overview and operational tracking
- Explain why separate frontend and backend services are better for public hosting
- Show environment-driven configuration for local and deployed environments

## Build Commands

### Backend jar

```bash
mvn clean package
```

### Frontend production build

```bash
cd frontend
npm install
npm run build
```

## Notes

- Uploaded resumes are stored in `uploads`
- Spring Boot uses `ddl-auto=update`
- `database/init.sql` is included for manual SQL-based setup if needed
- The sandbox here could not complete dependency downloads, so you should run the first full install from VS Code on your machine
