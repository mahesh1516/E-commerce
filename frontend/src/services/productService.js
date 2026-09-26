import api from './api';

/** Removes empty values so the URL stays clean. */
function cleanParams(params) {
  return Object.fromEntries(
    Object.entries(params || {}).filter(([, v]) => v !== undefined && v !== null && v !== '')
  );
}

const productService = {
  // params: { search, category, brand, minPrice, maxPrice, discounted, page, size, sort }
  getProducts: (params) => api.get('/products', { params: cleanParams(params) }).then((r) => r.data),
  getProduct: (id) => api.get(`/products/${id}`).then((r) => r.data),
  getBrands: () => api.get('/products/brands').then((r) => r.data),
  getCategories: () => api.get('/categories').then((r) => r.data),
  getCategory: (id) => api.get(`/categories/${id}`).then((r) => r.data),
};

export default productService;
