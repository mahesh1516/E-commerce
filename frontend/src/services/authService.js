import api from './api';

const authService = {
  login: (credentials) => api.post('/auth/login', credentials).then((r) => r.data),
  register: (data) => api.post('/auth/register', data).then((r) => r.data),
  getProfile: () => api.get('/users/me').then((r) => r.data),
  updateProfile: (data) => api.put('/users/me', data).then((r) => r.data),
};

export default authService;
