import apiClient from './api';

export default {
  // Lấy tất cả danh mục
  getAllCategories(page = 0, size = 50) {
    return apiClient.get('/categories', {
      params: {
        page,
        size
      }
    });
  },
  
  // Lấy danh mục theo ID
  getCategoryById(id) {
    return apiClient.get(`/categories/${id}`);
  },
  
  // Lấy sản phẩm theo danh mục
  getCategoryProducts(categoryId, availableOrdered = true, page = 0, size = 50) {
    // Nếu categoryId là null, gọi API với tham số đặc biệt để lấy sản phẩm không thuộc danh mục nào
    if (categoryId === null || categoryId === 'null') {
      return apiClient.get(`/categories/null/products`, {
        params: {
          availableOrdered,
          page,
          size
        }
      });
    }
    
    return apiClient.get(`/categories/${categoryId}/products`, {
      params: {
        availableOrdered,
        page,
        size
      }
    });
  }
}; 