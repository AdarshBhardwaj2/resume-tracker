import { useEffect, useState } from "react";
import {
  analyzeEmail,
  createApplication,
  deleteApplication,
  deleteEmailInsight,
  fetchApplications,
  fetchDashboard,
  fetchEmailInsights,
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

function App() {
  const [dashboard, setDashboard] = useState(null);
  const [applications, setApplications] = useState([]);
  const [emailInsights, setEmailInsights] = useState([]);
  const [applicationForm, setApplicationForm] = useState(emptyApplication);
  const [emailForm, setEmailForm] = useState(emptyEmail);
  const [resumeFile, setResumeFile] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [filters, setFilters] = useState({ status: "", keyword: "" });
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

  const statusOptions = ["SAVED", "APPLIED", "SHORTLISTED", "INTERVIEW", "OFFER", "REJECTED"];
  const priorityOptions = ["LOW", "MEDIUM", "HIGH"];

  const spotlightApplications = applications.filter((item) => item.priority === "HIGH").slice(0, 3);

  function updateApplicationField(event) {
    const { name, value } = event.target;
    setApplicationForm((current) => ({ ...current, [name]: value }));
  }

  function updateEmailField(event) {
    const { name, value } = event.target;
    setEmailForm((current) => ({ ...current, [name]: value }));
  }

  async function handleApplicationSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError("");
    setMessage("");
    try {
      const payload = {
        ...applicationForm,
        followUpDate: applicationForm.followUpDate || null
      };
      if (editingId) {
        await updateApplication(editingId, payload, resumeFile);
        setMessage("Application updated successfully.");
      } else {
        await createApplication(payload, resumeFile);
        setMessage("Application added successfully.");
      }
      resetApplicationForm();
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
    setError("");
    setMessage("");
    try {
      await analyzeEmail({
        ...emailForm,
        applicationId: emailForm.applicationId ? Number(emailForm.applicationId) : null
      });
      setEmailForm(emptyEmail);
      setMessage("Email analyzed successfully.");
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
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
    setResumeFile(null);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function resetApplicationForm() {
    setApplicationForm(emptyApplication);
    setEditingId(null);
    setResumeFile(null);
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

  return (
    <div className="page-shell">
      <header className="hero">
        <div>
          <p className="eyebrow">Spring Boot + React + PostgreSQL</p>
          <h1>Resume Tracker and Email Analyzer</h1>
          <p className="hero-copy">
            A portfolio-ready system to manage job applications, track resume versions,
            analyze recruiter emails, and monitor pipeline momentum.
          </p>
        </div>
        <div className="hero-panel">
          <div className="hero-stat">
            <span>Total Applications</span>
            <strong>{dashboard?.totalApplications ?? 0}</strong>
          </div>
          <div className="hero-stat">
            <span>Response Rate</span>
            <strong>{dashboard?.responseRate ?? 0}%</strong>
          </div>
          <div className="hero-stat">
            <span>Interviews</span>
            <strong>{dashboard?.interviewsScheduled ?? 0}</strong>
          </div>
        </div>
      </header>

      {error ? <div className="banner banner-error">{error}</div> : null}
      {message ? <div className="banner banner-success">{message}</div> : null}

      <main className="content-grid">
        <section className="panel full-span">
          <div className="section-head">
            <div>
              <p className="eyebrow">Insights</p>
              <h2>Pipeline Dashboard</h2>
            </div>
            <button className="ghost-button" onClick={loadData}>Refresh Data</button>
          </div>
          {loading || !dashboard ? (
            <p>Loading dashboard...</p>
          ) : (
            <>
              <div className="metric-grid">
                <MetricCard label="Active Pipelines" value={dashboard.activePipelines} />
                <MetricCard label="Follow-ups Due" value={dashboard.followUpsDue} />
                <MetricCard label="Top Companies" value={dashboard.topCompanies.length} />
                <MetricCard label="Email Insights" value={dashboard.recentEmailInsights.length} />
              </div>
              <div className="breakdown-grid">
                <BreakdownCard title="Status Breakdown" data={dashboard.statusBreakdown} />
                <BreakdownCard title="Priority Breakdown" data={dashboard.priorityBreakdown} />
                <div className="card highlight-card">
                  <h3>High-Priority Spotlight</h3>
                  {spotlightApplications.length === 0 ? (
                    <p>No high-priority applications yet.</p>
                  ) : (
                    spotlightApplications.map((item) => (
                      <div key={item.id} className="spotlight-item">
                        <strong>{item.company}</strong>
                        <span>{item.role}</span>
                        <small>{item.status} | Follow-up: {item.followUpDate || "Not set"}</small>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </>
          )}
        </section>

        <section className="panel">
          <div className="section-head">
            <div>
              <p className="eyebrow">Applications</p>
              <h2>{editingId ? "Update Application" : "Add Application"}</h2>
            </div>
            {editingId ? (
              <button className="ghost-button" onClick={resetApplicationForm}>
                Cancel Edit
              </button>
            ) : null}
          </div>
          <form className="form-grid" onSubmit={handleApplicationSubmit}>
            <input name="company" value={applicationForm.company} onChange={updateApplicationField} placeholder="Company" required />
            <input name="role" value={applicationForm.role} onChange={updateApplicationField} placeholder="Role" required />
            <input name="location" value={applicationForm.location} onChange={updateApplicationField} placeholder="Location" required />
            <input name="source" value={applicationForm.source} onChange={updateApplicationField} placeholder="Source" required />
            <select name="status" value={applicationForm.status} onChange={updateApplicationField}>
              {statusOptions.map((option) => <option key={option} value={option}>{option}</option>)}
            </select>
            <select name="priority" value={applicationForm.priority} onChange={updateApplicationField}>
              {priorityOptions.map((option) => <option key={option} value={option}>{option}</option>)}
            </select>
            <label>
              Applied Date
              <input type="date" name="appliedDate" value={applicationForm.appliedDate} onChange={updateApplicationField} required />
            </label>
            <label>
              Follow-up Date
              <input type="date" name="followUpDate" value={applicationForm.followUpDate} onChange={updateApplicationField} />
            </label>
            <input name="compensation" value={applicationForm.compensation} onChange={updateApplicationField} placeholder="Expected or offered CTC" />
            <input name="techStack" value={applicationForm.techStack} onChange={updateApplicationField} placeholder="Tech stack" />
            <input name="tags" value={applicationForm.tags} onChange={updateApplicationField} placeholder="Tags separated by commas" />
            <label className="file-field">
              Resume File
              <input type="file" accept=".pdf,.doc,.docx" onChange={(event) => setResumeFile(event.target.files?.[0] ?? null)} />
            </label>
            <textarea name="notes" value={applicationForm.notes} onChange={updateApplicationField} placeholder="Notes, action items, or recruiter comments" rows="5" />
            <button className="primary-button" disabled={submitting}>
              {submitting ? "Saving..." : editingId ? "Update Application" : "Create Application"}
            </button>
          </form>
        </section>

        <section className="panel">
          <div className="section-head">
            <div>
              <p className="eyebrow">Email Intelligence</p>
              <h2>Analyze Recruiter Emails</h2>
            </div>
          </div>
          <form className="form-grid" onSubmit={handleEmailSubmit}>
            <input name="sender" value={emailForm.sender} onChange={updateEmailField} placeholder="Sender email" required />
            <input name="subject" value={emailForm.subject} onChange={updateEmailField} placeholder="Email subject" required />
            <select name="applicationId" value={emailForm.applicationId} onChange={updateEmailField}>
              <option value="">Link to application (optional)</option>
              {applications.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.company} - {item.role}
                </option>
              ))}
            </select>
            <textarea name="body" value={emailForm.body} onChange={updateEmailField} placeholder="Paste the email body here" rows="9" required />
            <button className="primary-button" disabled={submitting}>
              {submitting ? "Analyzing..." : "Analyze Email"}
            </button>
          </form>
        </section>

        <section className="panel full-span">
          <div className="section-head">
            <div>
              <p className="eyebrow">Tracker</p>
              <h2>Application Board</h2>
            </div>
            <div className="filter-row">
              <input
                value={filters.keyword}
                onChange={(event) => setFilters((current) => ({ ...current, keyword: event.target.value }))}
                placeholder="Search by company or role"
              />
              <select
                value={filters.status}
                onChange={(event) => setFilters((current) => ({ ...current, status: event.target.value }))}
              >
                <option value="">All statuses</option>
                {statusOptions.map((option) => <option key={option} value={option}>{option}</option>)}
              </select>
            </div>
          </div>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Company</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Priority</th>
                  <th>Applied</th>
                  <th>Follow-up</th>
                  <th>Resume</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {applications.map((item) => (
                  <tr key={item.id}>
                    <td>{item.company}</td>
                    <td>{item.role}</td>
                    <td><span className={`pill pill-${item.status.toLowerCase()}`}>{item.status}</span></td>
                    <td>{item.priority}</td>
                    <td>{item.appliedDate}</td>
                    <td>{item.followUpDate || "-"}</td>
                    <td>
                      {item.resumeUrl ? (
                        <a href={`http://localhost:8080${item.resumeUrl}`} target="_blank" rel="noreferrer">
                          {item.resumeFileName}
                        </a>
                      ) : "No file"}
                    </td>
                    <td className="action-row">
                      <button className="ghost-button" onClick={() => startEdit(item)}>Edit</button>
                      <button className="danger-button" onClick={() => handleDeleteApplication(item.id)}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="panel full-span">
          <div className="section-head">
            <div>
              <p className="eyebrow">Analyzer Output</p>
              <h2>Email Insights Feed</h2>
            </div>
          </div>
          <div className="insight-grid">
            {emailInsights.map((item) => (
              <article key={item.id} className="insight-card">
                <div className="insight-head">
                  <div>
                    <h3>{item.subject}</h3>
                    <p>{item.sender}</p>
                  </div>
                  <button className="danger-button" onClick={() => handleDeleteEmail(item.id)}>Delete</button>
                </div>
                <div className="tag-row">
                  <span className="tag">{item.category}</span>
                  <span className="tag">{item.detectedStage}</span>
                  <span className="tag">{item.urgency} urgency</span>
                  <span className="tag">{item.tone} tone</span>
                  <span className="tag">{item.confidenceScore}% confidence</span>
                </div>
                {item.applicationLabel ? <p className="linked-app">Linked: {item.applicationLabel}</p> : null}
                <p><strong>Summary:</strong> {item.summary}</p>
                <p><strong>Suggested action:</strong> {item.suggestedAction}</p>
              </article>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

function MetricCard({ label, value }) {
  return (
    <div className="card metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function BreakdownCard({ title, data }) {
  return (
    <div className="card">
      <h3>{title}</h3>
      <div className="stack-list">
        {Object.entries(data || {}).map(([label, value]) => (
          <div key={label} className="stack-row">
            <span>{label}</span>
            <strong>{value}</strong>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
