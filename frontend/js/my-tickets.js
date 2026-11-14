import './layout.js';
import { api } from './api.js';
import { FARE_LABELS, clearError, dateTime, esc, formatCountdown, getOwnerId, showError, toast } from './ui.js';

const ownerId = getOwnerId();
const list = document.getElementById('ticket-list');
const errorBox = document.getElementById('tickets-error');

const STATUS = {
  ACTIVE: { label: 'Ważny', order: 0 },
  NOT_VALIDATED: { label: 'Do skasowania', order: 1 },
  EXPIRED: { label: 'Wygasły', order: 2 },
};

let tickets = [];
const handledExpiries = new Set();

function card(ticket) {
  let body;
  if (ticket.status === 'ACTIVE') {
    body = `
      <p class="countdown" data-expires="${esc(ticket.expiresAt)}" aria-label="Pozostały czas">--:--</p>
      <p class="muted">Ważny do ${dateTime(ticket.expiresAt)}</p>`;
  } else if (ticket.status === 'NOT_VALIDATED') {
    body = `
      <p class="muted">Kupiony ${dateTime(ticket.createdAt)}. Skasuj bilet po wejściu do pojazdu.</p>
      <button class="btn btn--primary" type="button" data-validate="${ticket.id}">Skasuj bilet</button>`;
  } else {
    body = `<p class="muted">Wygasł ${dateTime(ticket.expiresAt)}.</p>`;
  }

  return `
    <article class="card ticket-card">
      <div class="ticket-head">
        <h3>${esc(ticket.ticketType.name)}</h3>
        <span class="chip">${STATUS[ticket.status].label}</span>
      </div>
      <p class="muted">${FARE_LABELS[ticket.ticketType.fare]}, kod <span class="code">${esc(ticket.code)}</span></p>
      ${body}
    </article>`;
}

function render() {
  if (!tickets.length) {
    list.innerHTML = '<div class="empty">Nie masz jeszcze biletów. <a href="tickets.html">Kup pierwszy bilet</a>.</div>';
    return;
  }
  const sorted = [...tickets].sort((a, b) =>
    STATUS[a.status].order - STATUS[b.status].order || new Date(b.createdAt) - new Date(a.createdAt));
  list.innerHTML = sorted.map(card).join('');
  tick();
}

async function load() {
  try {
    tickets = await api.tickets.list(ownerId);
    clearError(errorBox);
    render();
  } catch (error) {
    list.innerHTML = '';
    showError(errorBox, error);
  }
}

function tick() {
  let reload = false;
  document.querySelectorAll('[data-expires]').forEach((el) => {
    const remaining = new Date(el.dataset.expires) - Date.now();
    if (remaining > 0) {
      el.textContent = formatCountdown(remaining);
    } else {
      el.textContent = '00:00';
      if (!handledExpiries.has(el.dataset.expires)) {
        handledExpiries.add(el.dataset.expires);
        reload = true;
      }
    }
  });
  if (reload) load();
}

list.addEventListener('click', async (event) => {
  const button = event.target.closest('[data-validate]');
  if (!button) return;
  if (!confirm('Skasować bilet? Czas ważności zacznie biec od razu i nie da się tego cofnąć.')) return;
  button.disabled = true;
  try {
    await api.tickets.validate(button.dataset.validate, ownerId);
    toast('Bilet skasowany.');
    await load();
  } catch (error) {
    toast(error.message, 'error');
    button.disabled = false;
  }
});

setInterval(tick, 1000);
load();
