import axios from 'axios';

export const AUTH_STORAGE_KEY = 'nexora_auth';

export function getStoredAuth() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!raw) return null;
    const auth = JSON.parse(raw);
    // Drop expired tokens
    if (auth.expiresAt && Date.now() > auth.expiresAt) {
      localStorage.removeItem(AUTH_STORAGE_KEY);
      return null;
    }
    return auth;
  } catch {
    return null;
  }
}

export function storeAuth(auth) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth));
}

export function clearStoredAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}

/** One configured Axios instance used by every service. */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

// REQUEST interceptor: attach "Authorization: Bearer <token>" automatically
api.interceptors.request.use((config) => {
  const auth = getStoredAuth();
  if (auth?.token) {
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});

// RESPONSE interceptor: if a logged-in user gets 401 (token expired),
// log them out everywhere.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && getStoredAuth()) {
      clearStoredAuth();
      window.dispatchEvent(new Event('nexora:logout'));
    }
    return Promise.reject(error);
  }
);

/** Turns any API error into a readable message. */
export function getErrorMessage(error) {
  const data = error?.response?.data;
  if (data?.errors) return Object.values(data.errors).join('. ');
  if (data?.message) return data.message;
  if (error?.code === 'ERR_NETWORK') return 'Cannot reach the server. Is the backend running on port 8080?';
  return error?.message || 'Something went wrong';
}

/** Field-level validation errors from the backend, e.g. { email: "..." } */
export function getFieldErrors(error) {
  return error?.response?.data?.errors || {};
}

export default api;
