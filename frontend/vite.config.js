import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev server on :5173. Requests to /api are forwarded to Spring Boot on :8080,
// so the browser sees one origin (no CORS issues during development).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
