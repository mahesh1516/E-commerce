import api from './api';

const addressService = {
  getAll: () => api.get('/addresses').then((r) => r.data),
  create: (data) => api.post('/addresses', data).then((r) => r.data),
  update: (id, data) => api.put(`/addresses/${id}`, data).then((r) => r.data),
  remove: (id) => api.delete(`/addresses/${id}`).then((r) => r.data),
  setDefault: (id) => api.put(`/addresses/${id}/default`).then((r) => r.data),
};

export default addressService;
