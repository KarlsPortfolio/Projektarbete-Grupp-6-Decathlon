const el = (id) => document.getElementById(id);
const err = el("error");
const msg = el("msg");

const modes = {
  decathlon: {
    apiName: "Decathlon",
    standingsKey: "decathlon",
    title: "Decathlon Web MVP",
    events: [
      { id: "100m", label: "100m (s)", min: 5, max: 20 },
      { id: "longJump", label: "Long Jump (cm)", min: 0, max: 1000 },
      { id: "shotPut", label: "Shot Put (m)", min: 0, max: 30 },
      { id: "highJump", label: "High Jump (cm)", min: 0, max: 300 },
      { id: "400m", label: "400m (s)", min: 20, max: 100 },
      { id: "110mHurdles", label: "110m Hurdles (s)", min: 10, max: 30 },
      { id: "discusThrow", label: "Discus Throw (m)", min: 0, max: 85 },
      { id: "poleVault", label: "Pole Vault (cm)", min: 0, max: 1000 },
      { id: "javelinThrow", label: "Javelin Throw (m)", min: 0, max: 110 },
      { id: "1500m", label: "1500m (s)", min: 150, max: 400 }
    ]
  },
  heptathlon: {
    apiName: "Heptathlon",
    standingsKey: "heptathlon",
    title: "Heptathlon Web MVP",
    events: [
      { id: "100mHurdles", label: "100m Hurdles (s)", min: 10, max: 30 },
      { id: "highJump", label: "High Jump (cm)", min: 0, max: 300 },
      { id: "shotPut", label: "Shot Put (m)", min: 0, max: 30 },
      { id: "200m", label: "200m (s)", min: 20, max: 100 },
      { id: "longJump", label: "Long Jump (cm)", min: 0, max: 1000 },
      { id: "javelinThrow", label: "Javelin Throw (m)", min: 0, max: 110 },
      { id: "800m", label: "800m (s)", min: 70, max: 250 }
    ]
  }
};

function getCurrentModeKey() {
  return document.querySelector('input[name="mode"]:checked')?.value || "decathlon";
}

function getCurrentMode() {
  return modes[getCurrentModeKey()];
}

function getCurrentEvents() {
  return getCurrentMode().events;
}

function setError(text) {
  err.textContent = text;
  msg.textContent = "";
}

function setMsg(text) {
  msg.textContent = text;
  err.textContent = "";
}

function updatePageTitle() {
  const title = getCurrentMode().title;
  document.title = title;
  el("pageTitle").textContent = title;
}

function renderEventOptions() {
  const eventSelect = el("event");
  const currentValue = eventSelect.value;
  const events = getCurrentEvents();

  eventSelect.innerHTML = events
    .map(event => `<option value="${event.id}">${event.label}</option>`)
    .join("");

  if (events.some(event => event.id === currentValue)) {
    eventSelect.value = currentValue;
  }
}

function renderStandingsHeader() {
  const headers = ["<th>Name</th>"]
    .concat(getCurrentEvents().map(event => `<th>${event.label.replace(/\s+\([^)]+\)$/, "")}</th>`))
    .concat("<th>Total</th>")
    .concat("<th>Delete</th>")
    .join("");

  el("standingsHeader").innerHTML = `<tr>${headers}</tr>`;
}

async function refreshModeView() {
  updatePageTitle();
  renderEventOptions();
  renderStandingsHeader();
  await renderStandings();
}

document.querySelectorAll('input[name="mode"]').forEach(radio => {
  radio.addEventListener("change", refreshModeView);
});

el("add").addEventListener("click", async () => {
  const name = el("name").value.trim();
  const multiEventType = getCurrentMode().apiName;

  if (!name) {
    setError("Name is required");
    return;
  }

  try {
    const res = await fetch("/api/competitors", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, multiEventType })
    });

    if (!res.ok) {
      const t = await res.text();
      setError(t || "Failed to add competitor");
      return;
    }

    setMsg("Added");
    el("name").value = "";
    await renderStandings();
  } catch (e) {
    setError("Network error");
  }
});

el("save").addEventListener("click", async () => {
  const name = el("name2").value.trim();
  const event = el("event").value;
  const rawText = el("raw").value.trim();
  const multiEventType = getCurrentMode().apiName;

  if (!name) {
    setError("Name is required");
    return;
  }

  if (!rawText) {
    setError("Result is required");
    return;
  }

  const raw = parseFloat(rawText);

if (raw < 0) {
       setError("Result cannot be negative");
       return;
     }

  if (Number.isNaN(raw)) {
    setError("Result is required");
    return;
  }

  const limits = getCurrentEvents().find(e => e.id === event);
  if (limits && (raw < limits.min || raw > limits.max)) {
    setError(`Result must be between ${limits.min} and ${limits.max}`);
    return;
  }

  try {
    const res = await fetch("/api/score", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, multiEventType, event, raw })
    });

    if (!res.ok) {
      const t = await res.text();
      setError(t || "Score failed");
      return;
    }

    const json = await res.json();
    setMsg(`Saved: ${json.points} pts`);
    el("raw").value = "";
    await renderStandings();
  } catch (e) {
    setError("Score failed");
  }
});

el("export").addEventListener("click", async () => {
  try {
    const res = await fetch("/api/export.csv");
    if (!res.ok) {
      setError("Export failed");
      return;
    }

    const text = await res.text();
    const blob = new Blob([text], { type: "text/csv;charset=utf-8" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "results.csv";
    a.click();
  } catch (e) {
    setError("Export failed");
  }
});

el("import").addEventListener("click", async () => {
  const fileInput = el("importFile");
  const file = fileInput.files[0];

  if (!file) {
    setError("File is required");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await fetch("/api/import.csv", {
      method: "POST",
      body: formData
    });

    if (!res.ok) {
      const t = await res.text();
      setError(t || "Import failed");
      return;
    }

    setMsg("Import completed");
    fileInput.value = "";
    await renderStandings();
  } catch (e) {
    setError("Import failed");
  }
});

async function deleteCompetitor(name) {
  const multiEventType = getCurrentMode().apiName;
  const confirmed = window.confirm(`Delete competitor ${name}?`);

  if (!confirmed) {
    return;
  }

  try {
    const res = await fetch("/api/delete", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, multiEventType })
    });

    if (!res.ok) {
      const t = await res.text();
      setError(t || "Delete failed");
      return;
    }

    setMsg("Competitor deleted");
    await renderStandings();
  } catch (e) {
    setError("Delete failed");
  }
}

async function renderStandings() {
  try {
    const res = await fetch("/api/standings");

    if (!res.ok) {
      setError("Could not load standings");
      return;
    }

    const data = await res.json();
    const mode = getCurrentMode();
    const list = data[mode.standingsKey] || [];
    const events = mode.events;

    const rows = list.map(r => {
      const scores = r.scores || {};
      const eventCells = events.map(event => `<td>${scores[event.id] ?? ""}</td>`).join("");
      const total = events.reduce((sum, event) => sum + (Number(scores[event.id]) || 0), 0);

      return `<tr>
        <td>${escapeHtml(r.name ?? "")}</td>
        ${eventCells}
        <td>${total}</td>
        <td><button type="button" class="delete-btn" data-name="${escapeHtmlAttr(r.name ?? "")}">🗑️</button></td>
      </tr>`;
    }).join("");

    el("standings").innerHTML = rows;

    document.querySelectorAll(".delete-btn").forEach(button => {
      button.addEventListener("click", () => deleteCompetitor(button.dataset.name));
    });
  } catch (e) {
    setError("Could not load standings");
  }
}

function escapeHtml(s) {
  return String(s).replace(/[&<>"]/g, c => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    "\"": "&quot;"
  }[c]));
}

function escapeHtmlAttr(s) {
  return String(s).replace(/["&<>]/g, c => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    "\"": "&quot;"
  }[c]));
}

refreshModeView();