import axios from 'axios'

// Base URL cho API
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8181/api/v1'

export const productService = {
  // Lấy danh sách sản phẩm
  getProducts(page = 0, size = 10) {
    return axios.get(`${API_URL}/products`, {
      params: { page, size }
    })
  },
  
  // Lấy chi tiết sản phẩm
  getProductById(id) {
    return axios.get(`${API_URL}/products/${id}`)
  },
  
  // Tạo sản phẩm mới
  createProduct(productData) {
    return axios.post(`${API_URL}/products`, productData)
  },
  
  // Cập nhật sản phẩm
  updateProduct(id, productData) {
    return axios.put(`${API_URL}/products/${id}`, productData)
  },
  
  // Xóa sản phẩm
  deleteProduct(id) {
    return axios.delete(`${API_URL}/products/${id}`)
  },

  // Lấy danh sách danh mục
  getCategories(page = 0, size = 100) {
    return axios.get(`${API_URL}/categories`, {
      params: { page, size }
    })
  },
  
  // Lấy chi tiết danh mục
  getCategoryById(id) {
    return axios.get(`${API_URL}/categories/${id}`)
  },
  
  // Tạo danh mục mới
  createCategory(categoryData) {
    return axios.post(`${API_URL}/categories`, categoryData)
  },
  
  // Cập nhật danh mục
  updateCategory(id, categoryData) {
    return axios.put(`${API_URL}/categories/${id}`, categoryData)
  },
  
  // Xóa danh mục
  deleteCategory(id) {
    return axios.delete(`${API_URL}/categories/${id}`)
  },

  // Lấy danh sách kích thước và đơn vị tính
  getSizeUnits(page = 0, size = 100) {
    return axios.get(`${API_URL}/size-units`, {
      params: { page, size }
    })
  },
  
  // Tạo kích thước/đơn vị tính mới
  createSizeUnit(sizeUnitData) {
    return axios.post(`${API_URL}/size-units`, sizeUnitData)
  },
  
  // Cập nhật kích thước/đơn vị tính
  updateSizeUnit(id, sizeUnitData) {
    return axios.put(`${API_URL}/size-units/${id}`, sizeUnitData)
  },
  
  // Xóa kích thước/đơn vị tính
  deleteSizeUnit(id) {
    return axios.delete(`${API_URL}/size-units/${id}`)
  }
} 