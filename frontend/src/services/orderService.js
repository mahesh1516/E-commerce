import api from './api';

const orderService = {
  // data: { addressId, paymentMethod: 'CARD' | 'COD', cardNumber, cardHolder }
  placeOrder: (data) => api.post('/orders', data).then((r) => r.data),
  getMyOrders: (page = 0, size = 10) => api.get('/orders', { params: { page, size } }).then((r) => r.data),
  getOrder: (id) => api.get(`/orders/${id}`).then((r) => r.data),
  cancelOrder: (id) => api.put(`/orders/${id}/cancel`).then((r) => r.data),
};

export default orderService;
