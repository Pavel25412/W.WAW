import './layout.js';
import { api } from './api.js';
import { TYPE_LABELS, clearError, esc, lineChip, showError, toast } from './ui.js';

const body = document.getElementById('lines-body');
const pageError = document.getElementById('lines-error');
const dialog = document.getElementById('line-dialog');
const form = document.getElementById('line-form');
const formError = document.getElementById('line-form-error');
const dialogTitle = document.getElementById('line-dialog-title');
const nameInput = form.elements.namedItem('name'); // form.name is a built-in property, not the field

let lines = [];
let editingId = null; // null = creating a new line

form.type.innerHTML = Object.entries(TYPE_LABELS)
  .map(([value, label]) => `<option value="${value}">${esc(label)}</option>`)
  .join('');

function render() {
  if (!lines.length) {
    body.innerHTML = '<tr><td colspan="4">Brak linii. Dodaj pierwszą.</td></tr>';
    return;
  }
  body.innerHTML = lines.map((l) => `
    <tr>
      <td>${lineChip(l.number)}</td>
      <td>${esc(TYPE_LABELS[l.type])}</td>
      <td>${esc(l.name)}</td>
      <td>
        <div class="actions">
          <button class="btn btn--ghost btn--small" type="button" data-edit="${l.id}">Edytuj</button>
          <button class="btn btn--ghost btn--small" type="button" data-delete="${l.id}">Usuń</button>
        </div>
      </td>
    </tr>`).join('');
}

async function load() {
  try {
    lines = await api.lines.list();
    clearError(pageError);
    render();
  } catch (error) {
    body.innerHTML = '';
    showError(pageError, error);
  }
}

function openForm(line = null) {
  editingId = line?.id ?? null;
  dialogTitle.textContent = line ? 'Edytuj linię' : 'Dodaj linię';
  form.number.value = line?.number ?? '';
  form.type.value = line?.type ?? 'BUS';
  nameInput.value = line?.name ?? '';
  clearError(formError);
  dialog.showModal();
  form.number.focus();
}

document.getElementById('add-line').addEventListener('click', () => openForm());
document.getElementById('line-cancel').addEventListener('click', () => dialog.close());

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  clearError(formError);
  const payload = { number: form.number.value, type: form.type.value, name: nameInput.value };
  try {
    if (editingId === null) {
      await api.lines.create(payload);
      toast('Linia dodana.');
    } else {
      await api.lines.update(editingId, payload);
      toast('Linia zapisana.');
    }
    dialog.close();
    await load();
  } catch (error) {
    showError(formError, error);
  }
});

body.addEventListener('click', async (event) => {
  const editButton = event.target.closest('[data-edit]');
  if (editButton) {
    openForm(lines.find((l) => l.id === Number(editButton.dataset.edit)));
    return;
  }

  const deleteButton = event.target.closest('[data-delete]');
  if (!deleteButton) return;
  const line = lines.find((l) => l.id === Number(deleteButton.dataset.delete));
  if (!confirm(`Usunąć linię ${line.number}? Razem z nią zniknął też jej dziennik opóźnień.`)) return;
  try {
    await api.lines.remove(line.id);
    toast('Linia usunięta.');
    await load();
  } catch (error) {
    toast(error.message, 'error');
  }
});

load();
