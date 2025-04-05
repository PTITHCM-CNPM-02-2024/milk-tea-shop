import apiClient from './api';

export default {
  /**
   * Lấy danh sách topping có sẵn
   * @param {number} page - Số trang (mặc định: 0)
   * @param {number} size - Kích thước trang (mặc định: 100)
   * @returns {Promise} Phản hồi từ API
   */
  getAvailableToppings(page = 0, size = 100) {
    try {
      return apiClient.get('/products/topping', {
        params: { page, size }
      });
    } catch (error) {
      console.error('Lỗi khi lấy danh sách topping:', error);
      throw error;
    }
  },

  /**
   * Lấy danh sách topping không có sẵn
   * @param {number} page - Số trang (mặc định::0)
   * @param {number} size - Kích thước trang (mặc định: 100)
   * @returns {Promise} Phản hồi từ API
   */
  getUnavailableToppings(page = 0, size = 100) {
    try {
      return apiClient.get('products/unavailable-topping', {
        params: { page, size }
      });
    } catch (error) {
      console.error('Lỗi khi lấy danh sách topping không có sẵn:', error);
      throw error;
    }
  }
};