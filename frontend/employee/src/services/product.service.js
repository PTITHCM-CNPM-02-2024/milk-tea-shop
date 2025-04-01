import apiClient from './api';

export default {
  // Lấy tất cả sản phẩm
  getAllProducts() {
    return apiClient.get('/products');
  },
  
  // Lấy sản phẩm có sẵn để đặt hàng
  getAvailableProducts() {
    return apiClient.get('/products/available');
  },
  
  // Lấy sản phẩm không có sẵn để đặt hàng
  getUnavailableProducts() {
    return apiClient.get('/products/not-available');
  },
  
  // Lấy sản phẩm đặc trưng
  getSignatureProducts(isAvailableOrder = true) {
    return apiClient.get('/products/search', {
      params: {
        isAvailableOrder,
        isSignature: true
      }
    });
  },
  
  // Lấy tất cả danh mục
  getAllCategories() {
    return apiClient.get('/categories');
  }
};