import { useEffect, useState } from "react";
import {
  analyzeEmail,
  analyzeResumeMatch,
  createApplication,
  deleteApplication,
  deleteEmailInsight,
  fetchApplications,
  fetchDashboard,
  fetchEmailInsights,
  fetchInterviewPrep,
  updateApplication
} from "./api";

const emptyApplication = {
  company: "",
  role: "",
  location: "",
  source: "",
  status: "APPLIED",
  priority: "MEDIUM",
  appliedDate: new Date().toISOString().slice(0, 10),
  followUpDate: "",
  compensation: "",
  techStack: "",
  tags: "",
  notes: ""
};

const emptyEmail = {
  sender: "",
  subject: "",
  body: "",
  applicationId: ""
};

const emptyResumeMatch = {
  targetRole: "",
  resumeText: "",
  jobDescription: ""
};

const statusOptions = ["SAVED", "APPLIED", "SHORTLISTED", "INTERVIEW", "OFFER", "REJECTED"];
const priorityOptions = ["LOW", "MEDIUM", "HIGH"];
const tabs = ["overview", "applications", "emails", "resume-match", "interview-prep"];

function App() {
  const [dashboard, setDashboard] = useState(null);
  const [applications, setApplications] = useState([]);
  const [emailInsights, setEmailInsights] = useState([]);
  const [applicationForm, setApplicationForm] = useState(emptyApplication);
  const [emailForm, setEmailForm] = useState(emptyEmail);
  const [resumeMatchForm, setResumeMatchForm] = useState(emptyResumeMatch);
  const [resumeMatchResult, setResumeMatchResult] = useState(null);
  const [interviewRole, setInterviewRole] = useState("backend");
  const [interviewPrep, setInterviewPrep] = useState(null);
  const [resumeFile, setResumeFile] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [filters, setFilters] = useState({ status: "", keyword: "" });
  const [activeTab, setActiveTab] = useState("overview");
  const [sortBy, setSortBy] = useState("updated");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  async function loadData() {
    setLoading(true);
    setError("");
    try {
      const [dashboardData, applicationData, emailData] = await Promise.all([
        fetchDashboard(),
        fetchApplications(filters),
        fetchEmailInsights()
      ]);
      setDashboard(dashboardData);
      setApplications(applicationData);
      setEmailInsights(emailData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, [filters.status, filters.keyword]);

  useEffect(() => {
    async function loadInterviewPrep() {
      try {
        const data = await fetchInterviewPrep(interviewRole);
        setInterviewPrep(data);
      } catch (err) {
        setError(err.message);
      }
    }
    loadInterviewPrep();
  }, [interviewRole]);

  function updateApplicationField(event) {
    const { name, value } = event.target;
    setApplicationForm((current) => ({ ...current, [name]: value }));
  }

  function updateEmailField(event) {
    const { name, value } = event.target;
    setEmailForm((current) => ({ ...current, [name]: value }));
  }

  function updateResumeMatchField(event) {
    const { name, value } = event.target;
    setResumeMatchForm((current) => ({ ...current, [name]: value }));
  }

  async function handleApplicationSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setMessage("");
    setError("");
    try {
      const payload = {
        ...applicationForm,
        followUpDate: applicationForm.followUpDate || null
      };
      if (editingId) {
        await updateApplication(editingId, payload, resumeFile);
        setMessage("Application updated.");
      } else {
        await createApplication(payload, resumeFile);
        setMessage("Application created.");
      }
      setApplicationForm(emptyApplication);
      setEditingId(null);
      setResumeFile(null);
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleEmailSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setMessage("");
    setError("");
    try {
      await analyzeEmail({
        ...emailForm,
        applicationId: emailForm.applicationId ? Number(emailForm.applicationId) : null
      });
      setEmailForm(emptyEmail);
      setMessage("Email analyzed.");
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleResumeMatchSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setMessage("");
    setError("");
    try {
      const result = await analyzeResumeMatch(resumeMatchForm);
      setResumeMatchResult(result);
      setMessage("Resume match analysis completed.");
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDeleteApplication(id) {
    try {
      await deleteApplication(id);
      setMessage("Application deleted.");
      await loadData();
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDeleteEmail(id) {
    try {
      await deleteEmailInsight(id);
      setMessage("Email insight deleted.");
      await loadData();
    } catch (err) {
      setError(err.message);
    }
  }

  function startEdit(item) {
    setEditingId(item.id);
    setApplicationForm({
      company: item.company,
      role: item.role,
      location: item.location,
      source: item.source,
      status: item.status,
      priority: item.priority,
      appliedDate: item.appliedDate,
      followUpDate: item.followUpDate || "",
      compensation: item.compensation || "",
      techStack: item.techStack || "",
      tags: item.tags || "",
      notes: item.notes || ""
    });
    setActiveTab("applications");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const topFocus = dashboard?.focusAreas?.slice(0, 3) ?? [];
  const latestApplications = [...applications].slice(0, 6);
  const sortedApplications = sortApplications(applications, sortBy);
  const upcomingTasks = buildUpcomingTasks(applications, emailInsights);
  const statusBreakdown = Object.entries(dashboard?.statusBreakdown ?? {});
  const recentEmails = emailInsights.slice(0, 4);
  const prepTopicDetails = interviewPrep?.topicDetails?.length
    ? interviewPrep.topicDetails
    : [
        "Explain the project structure clearly.",
        "Mention the main technologies used.",
        "Describe one challenge and how you solved it."
      ];
  const prepProjectTalkingPoints = interviewPrep?.projectTalkingPoints?.length
    ? interviewPrep.projectTalkingPoints
    : [
        "Explain the project in a simple flow.",
        "Mention one feature you are most confident about."
      ];
  const prepFinalTips = interviewPrep?.finalRoundTips?.length
    ? interviewPrep.finalRoundTips
    : [
        "Keep your project explanation short and clear.",
        "Be ready to explain future improvements."
      ];

  function exportApplications() {
    const headers = ["Company", "Role", "Status", "Priority", "Applied Date", "Follow Up Date", "Location", "Source", "Tech Stack", "Tags"];
    const rows = sortedApplications.map((item) => [
      item.company,
      item.role,
      item.status,
      item.priority,
      item.appliedDate ?? "",
      item.followUpDate ?? "",
      item.location ?? "",
      item.source ?? "",
      item.techStack ?? "",
      item.tags ?? ""
    ]);
    const csv = [headers, ...rows]
      .map((row) => row.map((value) => `"${String(value).replaceAll("\"", "\"\"")}"`).join(","))
      .join("\n");

    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "resume-tracker-applications.csv";
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <div className="shell">
      <header className="header">
        <div className="header-copy">
          <h1>Resume Tracker</h1>
          <p className="header-subtitle">Applications, resume match, email analysis, and interview preparation.</p>
        </div>
        <div className="header-actions">
          <button className="button button-secondary" onClick={() => setActiveTab("applications")}>Add Application</button>
          <button className="button button-primary" onClick={loadData}>Refresh</button>
        </div>
      </header>

      <section className="stats-row">
        <StatCard label="Applications" value={dashboard?.totalApplications ?? 0} />
        <StatCard label="Responses" value={`${dashboard?.responseRate ?? 0}%`} />
        <StatCard label="Interviews" value={`${dashboard?.interviewRate ?? 0}%`} />
        <StatCard label="Offers" value={dashboard?.offersReceived ?? 0} />
      </section>

      <nav className="tabbar">
        {tabs.map((tab) => (
          <button
            key={tab}
            className={`tab ${activeTab === tab ? "tab-active" : ""}`}
            onClick={() => setActiveTab(tab)}
          >
            {formatTab(tab)}
          </button>
        ))}
      </nav>

      {error ? <div className="notice notice-error">{error}</div> : null}
      {message ? <div className="notice notice-success">{message}</div> : null}

      {activeTab === "overview" ? (
        <section className="grid">
          <div className="card card-large overview-lead">
            <p className="section-title">Suggestions</p>
            <div className="list">
              {loading ? (
                <p className="muted">Loading...</p>
              ) : (
                topFocus.map((item) => (
                  <div key={item} className="list-item">
                    <span className="dot" />
                    <p>{item}</p>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="card summary-card-panel">
            <p className="section-title">Summary</p>
            <MetricLine label="Active applications" value={dashboard?.activePipelines ?? 0} />
            <MetricLine label="Follow-ups due" value={dashboard?.followUpsDue ?? 0} />
            <MetricLine label="Applied this week" value={dashboard?.applicationsThisWeek ?? 0} />
            <MetricLine label="Stale applications" value={dashboard?.staleApplications ?? 0} />
          </div>

          <div className="card card-large">
            <p className="section-title">Recent applications</p>
            <div className="compact-table">
              {latestApplications.map((item) => (
                <div key={item.id} className="compact-row">
                  <div>
                    <strong>{item.company}</strong>
                    <p className="muted">{item.role}</p>
                  </div>
                  <div className="compact-actions">
                    <span className="badge">{item.status}</span>
                    <span className="badge badge-soft">Score {computeApplicationScore(item)}</span>
                    <button className="text-button" onClick={() => startEdit(item)}>Edit</button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="card">
            <p className="section-title">Top companies</p>
            <div className="pill-list">
              {(dashboard?.topCompanies ?? []).map((item) => (
                <span key={item} className="pill">{item}</span>
              ))}
            </div>
          </div>

          <div className="card card-large feature-strip">
            <div>
              <p className="section-title">Quick access</p>
              <h2 className="feature-title">Resume check and interview prep</h2>
              <p className="muted">Use the analysis tools to improve your resume and prepare for role-specific interviews.</p>
            </div>
            <div className="feature-actions">
              <button className="button button-primary" onClick={() => setActiveTab("resume-match")}>Resume Match</button>
              <button className="button button-secondary" onClick={() => setActiveTab("interview-prep")}>Interview Prep</button>
            </div>
          </div>

          <div className="card card-large">
            <p className="section-title">Status overview</p>
            <div className="stack">
              {statusBreakdown.map(([label, value]) => (
                <div key={label} className="progress-item">
                  <div className="progress-head">
                    <span>{label}</span>
                    <strong>{value}</strong>
                  </div>
                  <div className="progress-track">
                    <div
                      className="progress-fill"
                      style={{ width: `${dashboard?.totalApplications ? (value / dashboard.totalApplications) * 100 : 0}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="card">
            <p className="section-title">Tasks</p>
            <div className="stack">
              {upcomingTasks.length === 0 ? (
                <p className="muted">No pending tasks.</p>
              ) : (
                upcomingTasks.map((task) => (
                  <div key={`${task.title}-${task.date}`} className="task-item">
                    <div>
                      <strong>{task.title}</strong>
                      <p className="muted">{task.detail}</p>
                    </div>
                    <span className="badge">{task.date}</span>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="card">
            <p className="section-title">Recent emails</p>
            <div className="stack">
              {recentEmails.map((item) => (
                <div key={item.id} className="email-mini">
                  <strong>{item.subject}</strong>
                  <p className="muted">{item.sender}</p>
                  <div className="pill-list">
                    <span className="pill">{item.category}</span>
                    <span className="pill">{item.urgency}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      ) : null}

      {activeTab === "applications" ? (
        <section className="grid">
          <div className="card card-large">
            <p className="section-title">{editingId ? "Update application" : "Add application"}</p>
            <form className="form-grid" onSubmit={handleApplicationSubmit}>
              <input className="input" name="company" value={applicationForm.company} onChange={updateApplicationField} placeholder="Company" required />
              <input className="input" name="role" value={applicationForm.role} onChange={updateApplicationField} placeholder="Role" required />
              <input className="input" name="location" value={applicationForm.location} onChange={updateApplicationField} placeholder="Location" required />
              <input className="input" name="source" value={applicationForm.source} onChange={updateApplicationField} placeholder="Source" required />
              <select className="input" name="status" value={applicationForm.status} onChange={updateApplicationField}>
                {statusOptions.map((option) => <option key={option} value={option}>{option}</option>)}
              </select>
              <select className="input" name="priority" value={applicationForm.priority} onChange={updateApplicationField}>
                {priorityOptions.map((option) => <option key={option} value={option}>{option}</option>)}
              </select>
              <input className="input" type="date" name="appliedDate" value={applicationForm.appliedDate} onChange={updateApplicationField} required />
              <input className="input" type="date" name="followUpDate" value={applicationForm.followUpDate} onChange={updateApplicationField} />
              <input className="input" name="compensation" value={applicationForm.compensation} onChange={updateApplicationField} placeholder="Compensation" />
              <input className="input" name="techStack" value={applicationForm.techStack} onChange={updateApplicationField} placeholder="Tech stack" />
              <input className="input" name="tags" value={applicationForm.tags} onChange={updateApplicationField} placeholder="Tags" />
              <input className="input" type="file" accept=".pdf,.doc,.docx" onChange={(event) => setResumeFile(event.target.files?.[0] ?? null)} />
              <textarea className="input textarea full-span" name="notes" value={applicationForm.notes} onChange={updateApplicationField} placeholder="Notes" rows="6" />
              <div className="actions full-span">
                <button className="button button-primary" disabled={submitting}>
                  {submitting ? "Saving..." : editingId ? "Save Changes" : "Create Application"}
                </button>
                {editingId ? (
                  <button type="button" className="button button-secondary" onClick={() => {
                    setEditingId(null);
                    setApplicationForm(emptyApplication);
                    setResumeFile(null);
                  }}>
                    Cancel
                  </button>
                ) : null}
              </div>
            </form>
          </div>

          <div className="card card-large">
            <div className="toolbar">
              <input
                className="input"
                value={filters.keyword}
                onChange={(event) => setFilters((current) => ({ ...current, keyword: event.target.value }))}
                placeholder="Search company, role, stack, tags"
              />
              <select
                className="input"
                value={filters.status}
                onChange={(event) => setFilters((current) => ({ ...current, status: event.target.value }))}
              >
                <option value="">All statuses</option>
                {statusOptions.map((option) => <option key={option} value={option}>{option}</option>)}
              </select>
              <select className="input" value={sortBy} onChange={(event) => setSortBy(event.target.value)}>
                <option value="updated">Sort by latest</option>
                <option value="score">Sort by score</option>
                <option value="company">Sort by company</option>
                <option value="followUp">Sort by follow-up</option>
              </select>
              <button className="button button-secondary" onClick={exportApplications}>Export CSV</button>
            </div>

            <div className="table">
              {sortedApplications.map((item) => (
                <div key={item.id} className="table-row">
                  <div className="table-main">
                    <strong>{item.company}</strong>
                    <p className="muted">{item.role} • {item.location}</p>
                  </div>
                  <span className="badge">{item.status}</span>
                  <span className="muted">{item.priority}</span>
                  <span className="badge badge-soft">Score {computeApplicationScore(item)}</span>
                  <div className="compact-actions">
                    {item.resumeUrl ? (
                      <a href={getFileUrl(item.resumeUrl)} target="_blank" rel="noreferrer">Resume</a>
                    ) : (
                      <span className="muted">No file</span>
                    )}
                    <button className="text-button" onClick={() => startEdit(item)}>Edit</button>
                    <button className="text-button danger" onClick={() => handleDeleteApplication(item.id)}>Delete</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      ) : null}

      {activeTab === "emails" ? (
        <section className="grid">
          <div className="card">
            <p className="section-title">Email analysis</p>
            <form className="form-grid" onSubmit={handleEmailSubmit}>
              <input className="input" name="sender" value={emailForm.sender} onChange={updateEmailField} placeholder="Sender email" required />
              <input className="input" name="subject" value={emailForm.subject} onChange={updateEmailField} placeholder="Subject" required />
              <select className="input" name="applicationId" value={emailForm.applicationId} onChange={updateEmailField}>
                <option value="">Link application</option>
                {applications.map((item) => <option key={item.id} value={item.id}>{item.company} - {item.role}</option>)}
              </select>
              <textarea className="input textarea full-span" name="body" value={emailForm.body} onChange={updateEmailField} placeholder="Paste email body" rows="10" required />
              <div className="actions full-span">
                <button className="button button-primary" disabled={submitting}>
                  {submitting ? "Analyzing..." : "Analyze Email"}
                </button>
                <button
                  type="button"
                  className="button button-secondary"
                  onClick={() => setEmailForm({
                    sender: "recruiter@example.com",
                    subject: "Interview scheduling for backend developer role",
                    body: "We would like to schedule your interview this week. Please share your availability by tomorrow.",
                    applicationId: ""
                  })}
                >
                  Use sample
                </button>
              </div>
            </form>
          </div>

          <div className="card card-large">
            <p className="section-title">Email results</p>
            <div className="stack">
              {emailInsights.map((item) => (
                <article key={item.id} className="insight">
                  <div className="insight-head">
                    <div>
                      <strong>{item.subject}</strong>
                      <p className="muted">{item.sender}</p>
                    </div>
                    <button className="text-button danger" onClick={() => handleDeleteEmail(item.id)}>Delete</button>
                  </div>
                  <div className="pill-list">
                    <span className="pill">{item.category}</span>
                    <span className="pill">{item.detectedStage}</span>
                    <span className="pill">{item.urgency}</span>
                    <span className="pill">{item.confidenceScore}%</span>
                  </div>
                  <p><strong>Summary:</strong> {item.summary}</p>
                  <p><strong>Action:</strong> {item.suggestedAction}</p>
                </article>
              ))}
            </div>
          </div>
        </section>
      ) : null}

      {activeTab === "resume-match" ? (
        <section className="grid">
          <div className="card card-large">
            <p className="section-title">Resume match</p>
            <form className="form-grid" onSubmit={handleResumeMatchSubmit}>
              <input className="input full-span" name="targetRole" value={resumeMatchForm.targetRole} onChange={updateResumeMatchField} placeholder="Target role, e.g. Backend Engineer Intern" required />
              <textarea className="input textarea" name="resumeText" value={resumeMatchForm.resumeText} onChange={updateResumeMatchField} placeholder="Paste resume text here" rows="16" required />
              <textarea className="input textarea" name="jobDescription" value={resumeMatchForm.jobDescription} onChange={updateResumeMatchField} placeholder="Paste job description here" rows="16" required />
              <div className="actions full-span">
                <button className="button button-primary" disabled={submitting}>
                  {submitting ? "Scoring..." : "Analyze Fit"}
                </button>
                <button
                  type="button"
                  className="button button-secondary"
                  onClick={() => setResumeMatchForm({
                    targetRole: "Backend Engineer Intern",
                    resumeText: "Summary Java developer with project experience in Spring Boot, REST API, PostgreSQL and Docker. Projects Built a resume tracker using React, Spring Boot and PostgreSQL. Optimized API response time by 30%. Skills Java Spring Boot PostgreSQL React Git Docker AWS Education Master's in Computer Science.",
                    jobDescription: "We are hiring a Backend Engineer Intern with strong Java, Spring Boot, REST API, SQL, PostgreSQL, Docker, AWS and system design fundamentals. Experience with backend projects, Git and problem solving is preferred."
                  })}
                >
                  Use sample
                </button>
              </div>
            </form>
          </div>

          <div className="card">
            <p className="section-title">Match result</p>
            {resumeMatchResult ? (
              <div className="stack">
                <div className="score-panel">
                  <span className="score-label">{resumeMatchResult.fitLabel}</span>
                  <strong>{resumeMatchResult.overallScore}/100</strong>
                  <p className="muted">{resumeMatchResult.summary}</p>
                </div>
                <div className="score-grid">
                  <MiniScoreCard label="Skills" value={resumeMatchResult.skillsScore} />
                  <MiniScoreCard label="ATS" value={resumeMatchResult.atsReadinessScore} />
                  <MiniScoreCard label="Role" value={resumeMatchResult.roleAlignmentScore} />
                </div>
                <MetricLine label="Skills score" value={`${resumeMatchResult.skillsScore}/100`} />
                <MetricLine label="ATS readiness" value={`${resumeMatchResult.atsReadinessScore}/100`} />
                <MetricLine label="Role alignment" value={`${resumeMatchResult.roleAlignmentScore}/100`} />
                <TagBlock title="Matched keywords" items={resumeMatchResult.matchedKeywords} />
                <TagBlock title="Missing keywords" items={resumeMatchResult.missingKeywords} />
                <TextList title="Strengths" items={resumeMatchResult.strengths} />
                <TextList title="How to improve" items={resumeMatchResult.recommendations} />
              </div>
            ) : (
              <p className="muted">Run the analysis to see the score, matched skills, missing keywords, and resume improvement guidance.</p>
            )}
          </div>
        </section>
      ) : null}

      {activeTab === "interview-prep" ? (
        <section className="grid">
          <div className="card card-large">
            <div className="toolbar">
              <div>
                <p className="section-title">Interview prep</p>
                <h2 className="feature-title">{interviewPrep?.role ?? "Interview Preparation"}</h2>
                <p className="muted">{interviewPrep?.focusSummary}</p>
              </div>
              <select className="input compact-input" value={interviewRole} onChange={(event) => setInterviewRole(event.target.value)}>
                <option value="backend">Backend</option>
                <option value="frontend">Frontend</option>
                <option value="fullstack">Full Stack</option>
                <option value="data">Data</option>
              </select>
            </div>

            <div className="prep-grid">
              <div className="prep-section prep-card">
                <p className="section-subtitle">Topics to revise</p>
                <DenseList items={interviewPrep?.coreTopics ?? []} />
              </div>
              <div className="prep-section prep-card">
                <p className="section-subtitle">What to explain clearly</p>
                <DenseList items={prepTopicDetails} />
              </div>
              <div className="prep-section prep-card">
                <p className="section-subtitle">Resume checklist</p>
                <DenseList items={interviewPrep?.resumeChecklist ?? []} />
              </div>
              <div className="prep-section prep-card">
                <p className="section-subtitle">Possible questions</p>
                <DenseList items={interviewPrep?.likelyQuestions ?? []} />
              </div>
              <div className="prep-section prep-card">
                <p className="section-subtitle">Project talking points</p>
                <DenseList items={prepProjectTalkingPoints} />
              </div>
              <div className="prep-section prep-card">
                <p className="section-subtitle">Final round tips</p>
                <DenseList items={prepFinalTips} />
              </div>
            </div>
          </div>

          <div className="card">
            <p className="section-title">Useful links</p>
            <div className="resource-list">
              {(interviewPrep?.resources ?? []).map((resource) => (
                <a key={resource.url} className="resource-link" href={resource.url} target="_blank" rel="noreferrer">
                  <strong>{resource.label}</strong>
                  <p className="muted resource-description">{resource.description}</p>
                  <span>{resource.url}</span>
                </a>
              ))}
            </div>
          </div>
        </section>
      ) : null}
    </div>
  );
}

function formatTab(tab) {
  return tab.split("-").map((part) => part[0].toUpperCase() + part.slice(1)).join(" ");
}

function StatCard({ label, value }) {
  return (
    <div className="stat-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function MetricLine({ label, value }) {
  return (
    <div className="metric-line">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function MiniScoreCard({ label, value }) {
  return (
    <div className="mini-score-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function TagBlock({ title, items }) {
  return (
    <div>
      <p className="section-subtitle">{title}</p>
      <div className="pill-list">
        {items.length ? items.map((item) => <span key={item} className="pill">{item}</span>) : <span className="muted">None</span>}
      </div>
    </div>
  );
}

function TextList({ title, items }) {
  return (
    <div>
      {title ? <p className="section-subtitle">{title}</p> : null}
      <div className="list">
        {items.map((item) => (
          <div key={item} className="list-item">
            <span className="dot" />
            <p>{item}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

function DenseList({ items }) {
  return (
    <div className="dense-list">
      {items.map((item, index) => (
        <div key={item} className="dense-item">
          <span className="dense-index">{String(index + 1).padStart(2, "0")}</span>
          <p>{item}</p>
        </div>
      ))}
    </div>
  );
}

function getFileUrl(resumeUrl) {
  const baseUrl = import.meta.env.VITE_FILE_BASE_URL || import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
  return `${baseUrl}${resumeUrl}`;
}

function computeApplicationScore(item) {
  let score = 50;
  score += ({ HIGH: 18, MEDIUM: 10, LOW: 4 }[item.priority] ?? 0);
  score += ({ OFFER: 28, INTERVIEW: 20, SHORTLISTED: 14, APPLIED: 8, SAVED: 3, REJECTED: -20 }[item.status] ?? 0);
  if (item.resumeUrl) {
    score += 6;
  }
  if (item.techStack) {
    score += 4;
  }
  if (item.followUpDate) {
    const days = Math.ceil((new Date(item.followUpDate) - new Date()) / 86400000);
    if (days <= 3) {
      score += 6;
    }
  }
  return Math.max(0, Math.min(100, score));
}

function buildUpcomingTasks(applications, emails) {
  const tasks = [];

  applications.forEach((item) => {
    if (item.followUpDate) {
      tasks.push({
        title: `Follow up with ${item.company}`,
        detail: item.role,
        date: item.followUpDate
      });
    }
    if (item.status === "INTERVIEW") {
      tasks.push({
        title: `Prepare for interview`,
        detail: `${item.company} - ${item.role}`,
        date: item.followUpDate || "Soon"
      });
    }
  });

  emails.forEach((item) => {
    if (item.urgency === "High") {
      tasks.push({
        title: "Reply to recruiter email",
        detail: item.subject,
        date: "Priority"
      });
    }
  });

  return tasks.slice(0, 6);
}

function sortApplications(applications, sortBy) {
  const items = [...applications];
  if (sortBy === "score") {
    return items.sort((a, b) => computeApplicationScore(b) - computeApplicationScore(a));
  }
  if (sortBy === "company") {
    return items.sort((a, b) => a.company.localeCompare(b.company));
  }
  if (sortBy === "followUp") {
    return items.sort((a, b) => (a.followUpDate || "9999-12-31").localeCompare(b.followUpDate || "9999-12-31"));
  }
  return items.sort((a, b) => new Date(b.updatedAt || 0) - new Date(a.updatedAt || 0));
}

export default App;
