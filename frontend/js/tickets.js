import './layout.js';
import { api } from './api.js';
import { FARE_LABELS, TYPE_LABELS, clearError, esc, getOwnerId, money, showError } from './ui.js';

const grid = document.getElementById('ticket-grid');
const dialog = document.getElementById('purchase-dialog');
const advisorForm = document.getElementById('advisor-form');
const advisorError = document.getElementById('advisor-error');
const advisorResult = document.getElementById('advisor-result');

let ticketTypes = [];

const currentFare = () => document.querySelector('input[name="fare"]:checked').value;

function renderCatalog() {
  const items = ticketTypes.filter((t) => t.fare === currentFare());
  grid.innerHTML = items.map((t) => `
    <article class="card ticket-card">
      <h3>${esc(t.name)}</h3>
      <p class="muted">${esc(t.description)}</p>
      <p class="price">${money(t.price)}</p>
      <button class="btn btn--primary" type="button" data-buy="${t.id}">Kup bilet</button>
    </article>`).join('');
}


function openPurchase(type) {
  const ownerId = getOwnerId();
  let order = null; // created once; a declined payment can be retried without creating a duplicate order

  dialog.innerHTML = `
    <form id="purchase-form" novalidate>
      <h2 id="purchase-title">${esc(type.name)} <span class="chip">${FARE_LABELS[type.fare]}</span></h2>
      <p class="muted">${esc(type.description)}</p>
      <label>Liczba biletów
        <input type="number" name="quantity" min="1" max="10" value="1" required>
      </label>
      <p>Do zapłaty: <strong id="purchase-total">${money(type.price)}</strong></p>
      <fieldset>
        <legend>Metoda płatności</legend>
        <label class="radio"><input type="radio" name="method" value="BLIK" checked> BLIK</label>
        <label class="radio"><input type="radio" name="method" value="CARD"> Karta płatnicza (symulacja)</label>
      </fieldset>
      <label id="blik-field">Kod BLIK
        <input name="blikCode" inputmode="numeric" maxlength="6" autocomplete="off" placeholder="123456">
      </label>
      <p class="hint">Płatność jest symulowana i nie pobiera pieniędzy. Kod 000000 symuluje odrzuconą płatność.</p>
      <p class="form-error" id="purchase-error" role="alert" hidden></p>
      <div class="dialog-actions">
        <button class="btn btn--ghost" type="button" data-close>Anuluj</button>
        <button class="btn btn--primary" type="submit">Zapłać</button>
      </div>
    </form>`;

  const form = dialog.querySelector('#purchase-form');
  const errorBox = dialog.querySelector('#purchase-error');
  const blikField = dialog.querySelector('#blik-field');
  const total = dialog.querySelector('#purchase-total');
  const submit = form.querySelector('[type="submit"]');

  const quantity = () => Number(form.quantity.value);

  form.quantity.addEventListener('input', () => {
    total.textContent = money(type.price * (quantity() || 0));
  });
  form.addEventListener('change', () => {
    blikField.hidden = form.elements.namedItem('method').value !== 'BLIK';
  });

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    clearError(errorBox);
    submit.disabled = true;
    const paymentMethod = form.elements.namedItem('method').value;
    try {
      order ??= await api.orders.create({ ownerId, ticketTypeId: type.id, quantity: quantity() });
      form.quantity.readOnly = true; // the order exists now - quantity is fixed
      await api.orders.pay(order.id, {
        ownerId,
        method: paymentMethod,
        blikCode: paymentMethod === 'BLIK' ? form.blikCode.value.trim() : null,
      });
      showSuccess(type, order.quantity);
    } catch (error) {
      showError(errorBox, error);
    } finally {
      submit.disabled = false;
    }
  });

  dialog.showModal();
}

function showSuccess(type, count) {
  dialog.innerHTML = `
    <h2 id="purchase-title">Płatność przyjęta</h2>
    <p>Kupiono: ${count} × ${esc(type.name)} (${FARE_LABELS[type.fare].toLowerCase()}).
       Bilety czekają w zakładce „Moje bilety”. Skasuj bilet po wejściu do pojazdu.</p>
    <div class="dialog-actions">
      <button class="btn btn--ghost" type="button" data-close>Zamknij</button>
      <a class="btn btn--primary" href="my-tickets.html">Moje bilety</a>
    </div>`;
}

dialog.addEventListener('click', (event) => {
  if (event.target.closest('[data-close]')) dialog.close();
});

grid.addEventListener('click', (event) => {
  const button = event.target.closest('[data-buy]');
  if (button) openPurchase(ticketTypes.find((t) => t.id === Number(button.dataset.buy)));
});

document.querySelectorAll('input[name="fare"]').forEach((input) => input.addEventListener('change', renderCatalog));


advisorForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  clearError(advisorError);
  try {
    const result = await api.advisor(advisorForm.lineId.value, advisorForm.plannedMinutes.value, currentFare());
    advisorResult.innerHTML = `
      <article class="card card--accent">
        <h3>${esc(result.recommended.name)}</h3>
        <p class="price">${money(result.recommended.price)}</p>
        <p>${esc(result.reason)}</p>
        <button class="btn btn--primary" type="button" id="advisor-buy">Kup ten bilet</button>
      </article>`;
    document.getElementById('advisor-buy').addEventListener('click', () => openPurchase(result.recommended));
  } catch (error) {
    advisorResult.innerHTML = '';
    showError(advisorError, error);
  }
});

async function init() {
  try {
    const [types, lines] = await Promise.all([api.ticketTypes.list(), api.lines.list()]);
    ticketTypes = types;
    renderCatalog();
    advisorForm.lineId.innerHTML = lines
      .map((l) => `<option value="${l.id}">${esc(l.number)} (${esc(TYPE_LABELS[l.type])}) ${esc(l.name)}</option>`)
      .join('');
  } catch (error) {
    grid.innerHTML = `<p class="form-error">${esc(error.message)}</p>`;
  }
}

init();
