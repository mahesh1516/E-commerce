import api from './api';

const reviewService = {
  getReviews: (productId) => api.get(`/products/${productId}/reviews`).then((r) => r.data),
  addReview: (productId, data) => api.post(`/products/${productId}/reviews`, data).then((r) => r.data),
};

export default reviewService;
