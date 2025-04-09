import apiClient from './api';

export default {
  // Lấy sản phẩm có sẵn để đặt hàng
  getAvailableProducts(available = true, page = 0, size = 50) {
    return apiClient.get(`/available-order/${available}`, {
      params: {
        page,
        size
      }
    });
  },
  
  // Lấy chi tiết sản phẩm theo ID
  getProductById(id) {
    return apiClient.get(`/products/${id}`);
  },
  
  // Lấy sản phẩm theo danh mục
  getProductsByCategory(categoryId, availableOrdered = true, page = 0, size = 50) {
    return apiClient.get(`/categories/${categoryId}/products`, {
      params: {
        availableOrdered,
        page,
        size
      }
    });
  }
};