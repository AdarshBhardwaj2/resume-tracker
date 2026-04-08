const API_BASE = "http://localhost:8080/api";

async function parseResponse(response) {
  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    throw new Error(errorBody.error || "Something went wrong");
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export async function fetchDashboard() {
  return parseResponse(await fetch(`${API_BASE}/dashboard/stats`));
}

export async function fetchApplications({ status = "", keyword = "" } = {}) {
  const params = new URLSearchParams();
  if (status) params.set("status", status);
  if (keyword) params.set("keyword", keyword);
  return parseResponse(await fetch(`${API_BASE}/applications?${params.toString()}`));
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
  return parseResponse(
    await fetch(`${API_BASE}/applications`, {
      method: "POST",
      body: formData
    })
  );
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
  return parseResponse(
    await fetch(`${API_BASE}/applications/${id}`, {
      method: "PUT",
      body: formData
    })
  );
}

export async function deleteApplication(id) {
  return parseResponse(
    await fetch(`${API_BASE}/applications/${id}`, {
      method: "DELETE"
    })
  );
}

export async function fetchEmailInsights() {
  return parseResponse(await fetch(`${API_BASE}/emails`));
}

export async function analyzeEmail(payload) {
  return parseResponse(
    await fetch(`${API_BASE}/emails/analyze`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
  );
}

export async function deleteEmailInsight(id) {
  return parseResponse(
    await fetch(`${API_BASE}/emails/${id}`, {
      method: "DELETE"
    })
  );
}
