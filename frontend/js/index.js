import './layout.js';
import { api } from './api.js';
import { TYPE_LABELS, esc, lineChip, ratingChip } from './ui.js';

const box = document.getElementById('ranking');
const note = document.getElementById('ranking-note');

async function loadRanking() {
  try {
    const stats = (await api.stats.lines()).filter((s) => s.reportCount > 0).slice(0, 5);
    if (!stats.length) {
      box.innerHTML = '<p class="muted">Brak zgłoszeń z ostatnich dni.</p>';
      return;
    }
    note.textContent = `Linie z najwyższym średnim opóźnieniem w ostatnich ${stats[0].windowDays} dniach.`;
    box.innerHTML = stats.map((s) => `
      <div class="rank">
        ${lineChip(s.lineNumber)}
        <span>${esc(TYPE_LABELS[s.lineType])}</span>
        ${ratingChip(s.rating)}
        <strong>${s.averageDelayMinutes.toFixed(1)} min średnio</strong>
      </div>`).join('');
  } catch (error) {
    box.innerHTML = `<p class="form-error">${esc(error.message)}</p>`;
  }
}

loadRanking();
