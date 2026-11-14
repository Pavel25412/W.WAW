import './layout.js';
import { api } from './api.js';
import {
  TYPE_LABELS, clearError, daysLeft, dateTime, esc, lineChip, ratingChip, showError, toast,
} from './ui.js';

const form = document.getElementById('delay-form');
const formError = document.getElementById('delay-error');
const filter = document.getElementById('filter-line');
const reportList = document.getElementById('report-list');
const statsBody = document.getElementById('stats-body');
const retentionNote = document.getElementById('retention-note');

const lineLabel = (l) => `${l.number} (${TYPE_LABELS[l.type]}) ${l.name}`;

async function loadStats() {
  try {
    const stats = await api.stats.lines();
    if (stats.length) {
      retentionNote.textContent =
        `Zgłoszenia znikają automatycznie po ${stats[0].windowDays} dniach od dodania, bo opóźnienia zmieniają się z porą dnia i roku.`;
    }
    statsBody.innerHTML = stats.map((s) => `
      <tr>
        <td>${lineChip(s.lineNumber)} ${esc(TYPE_LABELS[s.lineType])}</td>
        <td class="num">${s.reportCount}</td>
        <td class="num">${s.reportCount ? s.averageDelayMinutes.toFixed(1) : '-'}</td>
        <td class="num">${s.reportCount ? s.p90DelayMinutes : '-'}</td>
        <td class="num">${s.reportCount ? s.maxDelayMinutes : '-'}</td>
        <td>${s.worstHour === null ? '-' : `${String(s.worstHour).padStart(2, '0')}:00`}</td>
        <td>${ratingChip(s.rating)}</td>
      </tr>`).join('');
  } catch (error) {
    statsBody.innerHTML = `<tr><td colspan="7">${esc(error.message)}</td></tr>`;
  }
}

function expiryText(report) {
  const days = daysLeft(report.expiresAt);
  return days <= 1 ? 'Zgłoszenie zniknie w ciągu doby.' : `Zgłoszenie zniknie za ${days} dni.`;
}

async function loadReports() {
  try {
    const reports = await api.delays.list(filter.value);
    if (!reports.length) {
      reportList.innerHTML = '<li class="empty">Brak zgłoszeń. Bądź pierwszy.</li>';
      return;
    }
    reportList.innerHTML = reports.map((r) => `
      <li class="report">
        <div class="report-head">${lineChip(r.lineNumber)} <strong>+${r.delayMinutes} min</strong></div>
        ${r.stopName ? `<p>Przystanek: ${esc(r.stopName)}</p>` : ''}
        ${r.comment ? `<p>${esc(r.comment)}</p>` : ''}
        <p class="muted">Dodano ${dateTime(r.reportedAt)}</p>
        <p class="hint">${expiryText(r)}</p>
      </li>`).join('');
  } catch (error) {
    reportList.innerHTML = `<li class="form-error">${esc(error.message)}</li>`;
  }
}

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  clearError(formError);
  try {
    await api.delays.create({
      lineId: Number(form.lineId.value),
      delayMinutes: Number(form.delayMinutes.value),
      stopName: form.stopName.value,
      comment: form.comment.value,
    });
    toast('Dziękujemy za zgłoszenie.');
    form.stopName.value = '';
    form.comment.value = '';
    await Promise.all([loadReports(), loadStats()]);
  } catch (error) {
    showError(formError, error);
  }
});

filter.addEventListener('change', loadReports);

async function init() {
  try {
    const lines = await api.lines.list();
    form.lineId.innerHTML = lines.map((l) => `<option value="${l.id}">${esc(lineLabel(l))}</option>`).join('');
    filter.innerHTML = '<option value="">Wszystkie linie</option>'
      + lines.map((l) => `<option value="${l.id}">${esc(lineLabel(l))}</option>`).join('');
  } catch (error) {
    showError(formError, error);
  }
  await Promise.all([loadReports(), loadStats()]);
}

init();
