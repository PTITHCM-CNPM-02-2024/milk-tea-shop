import api from './api'

export const productService = {
  // Lấy danh sách sản phẩm
  getProducts(page = 0, size = 10, filters = {}) {
    return api.get('/products', {
      params: { 
        page, 
        size,
        name: filters.name || undefined,
        categoryId: filters.categoryId || undefined,
        status: filters.status || undefined
      }
    })
  },
  
  // Lấy chi tiết sản phẩm
  getProductById(id) {
    return api.get(`/products/${id}`)
  },
  
  // Tạo sản phẩm mới
  createProduct(productData) {
    return api.post('/products', productData)
  },
  
  // Cập nhật sản phẩm
  updateProduct(id, productData) {
    return api.put(`/products/${id}`, productData)
  },
  
  // Xóa sản phẩm
  deleteProduct(id) {
    return api.delete(`/products/${id}`)
  },

  // Lấy danh sách danh mục
  getCategories(page = 0, size = 100, name = '') {
    return api.get('/categories', {
      params: { 
        page, 
        size,
        name: name || undefined
      }
    })
  },
  
  // Lấy chi tiết danh mục
  getCategoryById(id) {
    return api.get(`/categories/${id}`)
  },
  
  // Tạo danh mục mới
  createCategory(categoryData) {
    return api.post('/categories', categoryData)
  },
  
  // Cập nhật danh mục
  updateCategory(id, categoryData) {
    return api.put(`/categories/${id}`, categoryData)
  },
  
  // Xóa danh mục
  deleteCategory(id) {
    return api.delete(`/categories/${id}`)
  },

  // Lấy danh sách kích thước sản phẩm
  getProductSizes(page = 0, size = 100) {
    return api.get('/product-sizes', {
      params: { page, size }
    })
  },
  
  // Tạo kích thước sản phẩm mới
  createProductSize(sizeData) {
    return api.post('/product-sizes', sizeData)
  },
  
  // Cập nhật kích thước sản phẩm
  updateProductSize(id, sizeData) {
    return api.put(`/product-sizes/${id}`, sizeData)
  },
  
  // Xóa kích thước sản phẩm
  deleteProductSize(id) {
    return api.delete(`/product-sizes/${id}`)
  },

  // Lấy danh sách đơn vị tính
  getUnits(page = 0, size = 100) {
    return api.get('/units', {
      params: { page, size }
    })
  },
  
  // Tạo đơn vị tính mới
  createUnit(unitData) {
    return api.post('/units', unitData)
  },
  
  // Cập nhật đơn vị tính
  updateUnit(id, unitData) {
    return api.put(`/units/${id}`, unitData)
  },
  
  // Xóa đơn vị tính
  deleteUnit(id) {
    return api.delete(`/units/${id}`)
  }
} 