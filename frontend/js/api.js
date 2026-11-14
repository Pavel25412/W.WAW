import { API_BASE } from './config.js';

export class ApiError extends Error {
  constructor(status, message, fieldErrors = null) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request(path, { method = 'GET', body } = {}) {
  let response;
  try {
    response = await fetch(API_BASE + path, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(0, 'Nie można połączyć się z serwerem. Sprawdź, czy backend działa na http://localhost:8080.');
  }

  if (response.status === 204) return null;

  const data = await response.json().catch(() => null);
  if (!response.ok) {
    throw new ApiError(response.status, data?.message ?? 'Wystąpił nieoczekiwany błąd.', data?.fieldErrors ?? null);
  }
  return data;
}

function query(params) {
  const search = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && value !== '') search.set(key, value);
  }
  const text = search.toString();
  return text ? `?${text}` : '';
}

export const api = {
  lines: {
    list: () => request('/lines'),
    create: (body) => request('/lines', { method: 'POST', body }),
    update: (id, body) => request(`/lines/${id}`, { method: 'PUT', body }),
    remove: (id) => request(`/lines/${id}`, { method: 'DELETE' }),
  },
  ticketTypes: {
    list: (fare) => request(`/ticket-types${query({ fare })}`),
  },
  orders: {
    create: (body) => request('/orders', { method: 'POST', body }),
    pay: (id, body) => request(`/orders/${id}/payment`, { method: 'POST', body }),
  },
  tickets: {
    list: (ownerId) => request(`/tickets${query({ ownerId })}`),
    validate: (id, ownerId) => request(`/tickets/${id}/validate${query({ ownerId })}`, { method: 'POST' }),
  },
  delays: {
    list: (lineId) => request(`/delays${query({ lineId })}`),
    create: (body) => request('/delays', { method: 'POST', body }),
  },
  stats: {
    lines: () => request('/stats/lines'),
  },
  advisor: (lineId, plannedMinutes, fare) => request(`/advisor${query({ lineId, plannedMinutes, fare })}`),
};
