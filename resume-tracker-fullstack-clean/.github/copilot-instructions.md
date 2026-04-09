# Copilot instructions for Resume Tracker + Email Analyzer

This project is a full‑stack Java (Spring Boot) backend with a React (Vite) frontend. Keep guidance short and specific — below are the high‑value facts an AI coding agent needs to be productive immediately.

- Project roots:
  - Backend: `src/main/java/com/resume/tracker` (controllers, services, repository, dto, mapper)
  - Frontend: `frontend/` (Vite + React). Key files: `frontend/src/App.jsx`, `frontend/src/api.js`, `frontend/package.json`
  - DB helpers: `database/init.sql`

- Architecture and important boundaries:
  - REST API served by Spring Boot at `http://localhost:8080` (controllers: `DashboardController`, `EmailAnalysisController`, `JobApplicationController`).
  - Frontend calls the backend via `frontend/src/api.js`. Resume download links are absolute to the backend: `http://localhost:8080${resumeUrl}` (see `App.jsx`).
  - Service layer isolates business logic (`*Service.java`). Repositories are Spring Data JPA interfaces under `repository/`.
  - DTOs and a mapper (`TrackerMapper.java`) are used to decouple API shapes from entities. Prefer changing DTOs + mapper over touching entities for API contract changes.

- Run / build / test (exact commands discovered in repository):
  - Start backend (dev): from project root: `mvn spring-boot:run` (Java 21, Maven 3.9+ required).
  - Package backend jar: `mvn clean package`.
  - Frontend (dev): `cd frontend && npm install && npm run dev` (uses Vite; dev server defaults to `http://localhost:5173`).
  - Frontend (prod build): `cd frontend && npm run build`.

- Environment and DB:
  - Uses PostgreSQL. Default properties shown in `src/main/resources/application.properties` but environment variables can override:
    - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (README includes example `psql -f database/init.sql`).

- Patterns and project-specific conventions:
  - Multipart resume uploads: controllers accept multipart form-data; storage handled by `ResumeStorageService`. Uploaded files are saved to an `uploads/` folder (see README and service implementation).
  - Follow-up dates: UI sends empty string when not set; backend expects `null` for missing follow-up dates — handle conversion accordingly.
  - Email analysis is rule-based and produces an `EmailAnalysis` DTO with fields like `category`, `detectedStage`, `urgency`, `tone`, `summary`, `suggestedAction`. Frontend maps these to the insights feed in `App.jsx`.
  - Caching: dashboard aggregation is cached (see `config/CacheConfig.java`) — be mindful when changing aggregation code (cache invalidation may be required).
  - Error handling: `ApiExceptionHandler` handles top-level REST errors; use `ResourceNotFoundException` to signal 404s.

- Key files to inspect when changing behavior:
  - `src/main/java/com/resume/tracker/controller/JobApplicationController.java` — CRUD + file upload endpoints for applications
  - `src/main/java/com/resume/tracker/service/ResumeStorageService.java` — file storage mechanics and URL generation
  - `src/main/java/com/resume/tracker/controller/EmailAnalysisController.java` and `service/EmailAnalysisService.java` — where email analysis logic resides
  - `frontend/src/api.js` and `frontend/src/App.jsx` — frontend API usage and UX flows (filters, file upload, email analyze form)

- API surface (common endpoints) — use these exact routes when coding frontend/back-end changes:
  - `GET /api/applications` (supports `status` and `keyword` query parameters)
  - `POST /api/applications` (multipart)
  - `PUT /api/applications/{id}` (multipart)
  - `DELETE /api/applications/{id}`
  - `GET /api/emails`, `POST /api/emails/analyze`, `DELETE /api/emails/{id}`
  - `GET /api/dashboard/stats`

- Developer guidance for common tasks (examples):
  - To add a new dashboard metric: update `DashboardService.java` aggregation, update `DashboardStatsResponse.java`, and clear cache settings in `CacheConfig.java` if needed. Update `frontend/src/App.jsx` to consume new fields.
  - To add a new property to applications (persisted): add column to entity + migration (or rely on `ddl-auto=update` for quick iteration), add DTO field, update `TrackerMapper.java`, update controller and frontend form (`App.jsx`) to include the field.

- Tests and verification:
  - There is a SpringBoot test in `src/test/java/.../ResumeTrackerApplicationTests.java`. Run backend tests with `mvn test`.
  - Frontend has no test harness by default; validate UI manually via `npm run dev`.

- Do NOT assume authentication is present — endpoints are open in this repo. Avoid adding auth without coordinating with the maintainer.

If anything above is unclear, tell me which area you want expanded (e.g., step-by-step for adding a new API, modifying file upload behavior, or adjusting caching). I'll iterate. 
