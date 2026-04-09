const API_ROOT = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8080").replace(/\/$/, "");
const API_BASE = `${API_ROOT}/api`;
const TOKEN_KEY = "resume_tracker_token";
const USER_KEY = "resume_tracker_user";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || "";
}

export function getStoredUser() {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function storeSession(authResponse) {
  localStorage.setItem(TOKEN_KEY, authResponse.token);
  localStorage.setItem(USER_KEY, JSON.stringify(authResponse.user));
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

async function parseResponse(response) {
  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    if (response.status === 401) {
      clearSession();
    }
    throw new Error(errorBody.error || "Something went wrong");
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

async function request(path, options = {}) {
  const headers = new Headers(options.headers || {});
  const token = getToken();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  return parseResponse(
    await fetch(`${API_BASE}${path}`, {
      ...options,
      headers
    })
  );
}

export async function register(payload) {
  return parseResponse(
    await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
  );
}

export async function login(payload) {
  return parseResponse(
    await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
  );
}

export async function fetchMe() {
  return request("/auth/me");
}

export async function fetchDashboard() {
  return request("/dashboard/stats");
}

export async function fetchApplications({ status = "", keyword = "" } = {}) {
  const params = new URLSearchParams();
  if (status) params.set("status", status);
  if (keyword) params.set("keyword", keyword);
  return request(`/applications?${params.toString()}`);
}

export async function createApplication(formValues, resumeFile) {
  const formData = new FormData();
  formData.append(
    "application",
    new Blob([JSON.stringify(formValues)], { type: "application/json" })
  );
  if (resumeFile) {
    formData.append("resume", resumeFile);
  }
  return request("/applications", {
    method: "POST",
    body: formData
  });
}

export async function updateApplication(id, formValues, resumeFile) {
  const formData = new FormData();
  formData.append(
    "application",
    new Blob([JSON.stringify(formValues)], { type: "application/json" })
  );
  if (resumeFile) {
    formData.append("resume", resumeFile);
  }
  return request(`/applications/${id}`, {
    method: "PUT",
    body: formData
  });
}

export async function deleteApplication(id) {
  return request(`/applications/${id}`, {
    method: "DELETE"
  });
}

export async function fetchEmailInsights() {
  return request("/emails");
}

export async function analyzeEmail(payload) {
  return request("/emails/analyze", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function deleteEmailInsight(id) {
  return request(`/emails/${id}`, {
    method: "DELETE"
  });
}

export async function analyzeResumeMatch(payload) {
  return request("/resume-match", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function fetchInterviewPrep(role) {
  const params = new URLSearchParams({ role });
  return request(`/interview-prep?${params.toString()}`);
}
