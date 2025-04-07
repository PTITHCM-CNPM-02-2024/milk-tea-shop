import apiClient from './api';

export default {
  // Lấy sản phẩm có sẵn để đặt hàng
  getAvailableProducts() {
    return apiClient.get('/products/available');
  }
};