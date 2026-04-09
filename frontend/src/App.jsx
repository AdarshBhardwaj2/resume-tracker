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
    <div>
      <h1>Resume Tracker</h1>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {message && <p style={{ color: "green" }}>{message}</p>}

      <h2>Add Application</h2>
      <form onSubmit={handleApplicationSubmit}>
        <input name="company" value={applicationForm.company} onChange={updateApplicationField} placeholder="Company" />
        <input name="role" value={applicationForm.role} onChange={updateApplicationField} placeholder="Role" />
        <button type="submit">{editingId ? "Update" : "Add"}</button>
      </form>

      <h2>Applications</h2>
      {applications.map((app) => (
        <div key={app.id}>
          {app.company} - {app.role}
          <button onClick={() => startEdit(app)}>Edit</button>
          <button onClick={() => handleDeleteApplication(app.id)}>Delete</button>
        </div>
      ))}
    </div>
  );
}

export default App;