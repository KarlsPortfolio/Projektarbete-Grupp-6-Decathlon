const el = (id) => document.getElementById(id);
const err = el('error');
const msg = el('msg');
const disciplineSelect = el('discipline');
const eventSelect = el('event');
const standingsHeader = el('standingsHeader');

const nameInput = el('name2');
const menu = el('competitorMenu');

let currentEvents = [];
let competitorNames = [];

function setError(text) { err.textContent = text; msg.textContent = ''; }
function setMsg(text) { msg.textContent = text; err.textContent = ''; }

async function loadEvents() {
  const discipline = disciplineSelect.value;
  try {
    const res = await fetch(`/api/events?discipline=${encodeURIComponent(discipline)}`);
    if (!res.ok) {
      setError(`Could not load events (status ${res.status})`);
      currentEvents = [];
    } else {
      currentEvents = await res.json();
    }
  } catch (e) {
    setError('Could not load events');
    currentEvents = [];
  }

  eventSelect.innerHTML = currentEvents
    .map(e => `<option value="${e.id}">${e.label} (${e.unit})</option>`)
    .join('');

  standingsHeader.innerHTML = '<th>Name</th>'
    + currentEvents.map(e => `<th>${e.label}</th>`).join('')
    + '<th>Total</th>';
}

async function loadCompetitors() {
  try {
    const res = await fetch(`/api/competitors?discipline=${encodeURIComponent(disciplineSelect.value)}`);
    competitorNames = res.ok ? await res.json() : [];
  } catch (e) {
    competitorNames = [];
  }
  if (!menu.hidden) {
    renderMenu(competitorNames);
  }
}

function renderMenu(names) {
  menu.innerHTML = names.map(n => `<li role="option">${escapeHtml(n)}</li>`).join('');
  menu.hidden = names.length === 0;
}

nameInput.addEventListener('focus', () => renderMenu(competitorNames));
nameInput.addEventListener('click', () => renderMenu(competitorNames));
nameInput.addEventListener('input', () => {
  const q = nameInput.value.trim().toLowerCase();
  renderMenu(competitorNames.filter(n => n.toLowerCase().includes(q)));
});
nameInput.addEventListener('blur', () => { menu.hidden = true; });
nameInput.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') { menu.hidden = true; }
});
menu.addEventListener('mousedown', (e) => {
  const li = e.target.closest('li');
  if (!li) return;
  e.preventDefault();
  nameInput.value = li.textContent;
  menu.hidden = true;
});

function parseResult(text) {
  const t = text.trim().replace(',', '.');
  if (t === '') {
    return { error: 'Please enter a result' };
  }
  if (!/^-?(\d+\.?\d*|\.\d+)$/.test(t)) {
    return { error: 'Please enter a valid number for the result' };
  }
  return { value: parseFloat(t) };
}

disciplineSelect.addEventListener('change', async () => {
  menu.hidden = true;
  menu.innerHTML = '';
  competitorNames = [];
  await loadEvents();
  await loadCompetitors();
  await renderStandings();
});

el('add').addEventListener('click', async () => {
  const name = el('name').value;
  try {
    const res = await fetch('/api/competitors', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, discipline: disciplineSelect.value })
    });
    if (!res.ok) {
      const t = await res.text();
      setError(t || 'Failed to add competitor');
    } else {
      setMsg('Added');
      el('name').value = '';
    }
    await loadCompetitors();
    await renderStandings();
  } catch (e) {
    setError('Network error');
  }
});

el('save').addEventListener('click', async () => {
  const parsed = parseResult(el('raw').value);
  if (parsed.error) {
    setError(parsed.error);
    return;
  }
  const body = {
    name: nameInput.value,
    event: eventSelect.value,
    raw: parsed.value
  };
  try {
    const res = await fetch('/api/score', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const t = await res.text();
      setError(t || 'Failed to save score');
    } else {
      const json = await res.json();
      setMsg(`Saved: ${json.points} pts`);
    }
    await renderStandings();
  } catch (e) {
    setError('Score failed');
  }
});

el('export').addEventListener('click', async () => {
  try {
    const res = await fetch(`/api/export.csv?discipline=${encodeURIComponent(disciplineSelect.value)}`);
    const text = await res.text();
    const blob = new Blob([text], { type: 'text/csv;charset=utf-8' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'results.csv';
    a.click();
  } catch (e) {
    setError('Export failed');
  }
});

async function renderStandings() {
  try {
    const res = await fetch(`/api/standings?discipline=${encodeURIComponent(disciplineSelect.value)}`);
    const data = await res.json();

    const rows = data
      .sort((a,b)=> (b.total||0)-(a.total||0))
      .map(r => `<tr>
        <td>${escapeHtml(r.name)}</td>
        ${currentEvents.map(e => `<td>${r.scores?.[e.id] ?? ''}</td>`).join('')}
        <td>${r.total ?? 0}</td>
      </tr>`).join('');

    el('standings').innerHTML = rows;
  } catch (e) {
    setError('Could not load standings');
  }
}

function escapeHtml(s){
  return String(s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
}

async function init() {
  await loadEvents();
  await loadCompetitors();
  await renderStandings();
}

init();