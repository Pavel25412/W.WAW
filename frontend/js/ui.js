
export const TYPE_LABELS = {
  BUS: 'Autobus', TRAM: 'Tramwaj', METRO: 'Metro', SKM: 'SKM', KM: 'KM', WKD: 'WKD',
};
export const FARE_LABELS = { NORMAL: 'Normalny', REDUCED: 'Ulgowy' };

export function esc(value) {
  return String(value ?? '').replace(/[&<>"']/g, (c) => (
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
  ));
}

const moneyFormat = new Intl.NumberFormat('pl-PL', { style: 'currency', currency: 'PLN' });
export const money = (value) => moneyFormat.format(value);

const dateFormat = new Intl.DateTimeFormat('pl-PL', {
  dateStyle: 'short', timeStyle: 'short', timeZone: 'Europe/Warsaw',
});
export const dateTime = (iso) => dateFormat.format(new Date(iso));

export const lineChip = (number) => `<span class="chip chip--line">${esc(number)}</span>`;

export function ratingChip(rating) {
  if (rating === 'N/A') return '<span class="chip chip--na" title="Za mało zgłoszeń">brak oceny</span>';
  return `<span class="chip chip--${esc(rating)}" title="Ocena wg średniego opóźnienia">${esc(rating)}</span>`;
}

export function daysLeft(iso) {
  return Math.ceil((new Date(iso) - Date.now()) / 86_400_000);
}

export function formatCountdown(ms) {
  const total = Math.max(0, Math.floor(ms / 1000));
  const h = Math.floor(total / 3600);
  const m = Math.floor((total % 3600) / 60);
  const s = total % 60;
  const pad = (n) => String(n).padStart(2, '0');
  return h > 0 ? `${h}:${pad(m)}:${pad(s)}` : `${pad(m)}:${pad(s)}`;
}

let fallbackOwnerId = null;
export function getOwnerId() {
  const key = 'wwaw.ownerId';
  try {
    let id = localStorage.getItem(key);
    if (!id) {
      id = crypto.randomUUID();
      localStorage.setItem(key, id);
    }
    return id;
  } catch {
    fallbackOwnerId ??= crypto.randomUUID();
    return fallbackOwnerId;
  }
}

export function toast(message, kind = 'info') {
  let container = document.getElementById('toasts');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toasts';
    container.setAttribute('role', 'status');
    document.body.append(container);
  }
  const el = document.createElement('div');
  el.className = `toast${kind === 'error' ? ' toast--error' : ''}`;
  el.textContent = message;
  container.append(el);
  setTimeout(() => el.remove(), 4500);
}

function errorText(error) {
  if (error?.fieldErrors && Object.keys(error.fieldErrors).length) {
    return Object.values(error.fieldErrors).join(' ');
  }
  return error?.message ?? 'Wystąpił nieoczekiwany błąd.';
}

export function showError(element, error) {
  element.textContent = errorText(error);
  element.hidden = false;
}

export function clearError(element) {
  element.textContent = '';
  element.hidden = true;
}
