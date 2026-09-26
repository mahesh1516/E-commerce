import api from './api';

const adminService = {
  getDashboard: () => api.get('/admin/dashboard').then((r) => r.data),
  getUsers: () => api.get('/admin/users').then((r) => r.data),

  getOrders: (params) => api.get('/admin/orders', { params }).then((r) => r.data),
  updateOrderStatus: (id, status) => api.put(`/admin/orders/${id}/status`, { status }).then((r) => r.data),

  getInventory: () => api.get('/admin/inventory').then((r) => r.data),
  updateInventory: (productId, data) => api.put(`/admin/inventory/${productId}`, data).then((r) => r.data),

  createProduct: (data) => api.post('/products', data).then((r) => r.data),
  updateProduct: (id, data) => api.put(`/products/${id}`, data).then((r) => r.data),
  deleteProduct: (id) => api.delete(`/products/${id}`).then((r) => r.data),

  createCategory: (data) => api.post('/categories', data).then((r) => r.data),
  updateCategory: (id, data) => api.put(`/categories/${id}`, data).then((r) => r.data),
  deleteCategory: (id) => api.delete(`/categories/${id}`).then((r) => r.data),
};

export default adminService;
