import apiClient from './api';

export default {
  // Lấy sản phẩm có sẵn để đặt hàng
  getAvailableProducts(available = true) {
    return apiClient.get(`products/available-order/${available}`);
  },
  
  // Lấy chi tiết sản phẩm theo ID
  getProductById(id) {
    return apiClient.get(`products/${id}`);
  }
};