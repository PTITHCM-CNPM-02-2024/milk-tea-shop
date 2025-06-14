import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { productService } from '@/services/productService'

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref([])
  const totalProducts = ref(0) 
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const categories = ref([])
  const productSizes = ref([])
  
  // Getter
  const totalPages = computed(() => Math.ceil(totalProducts.value / pageSize.value))
  
  // Lấy danh sách sản phẩm
  async function fetchProducts(page = 0, size = 10, filters = {}) {
    loading.value = true
    error.value = null
    
    try {
      currentPage.value = page
      pageSize.value = size
      
      const response = await productService.getProducts(page, size, filters)
      products.value = response.data.content || []
      totalProducts.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchProducts:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Lấy thông tin chi tiết sản phẩm
  async function fetchProductById(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.getProductById(id)
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchProductById:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải thông tin sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Tạo sản phẩm mới
  async function createProduct(productData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.createProduct(productData)
      // Cập nhật danh sách sản phẩm
      await fetchProducts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong createProduct:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tạo sản phẩm mới'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật sản phẩm
  async function updateProduct(id, productData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.updateProduct(id, productData)
      // Cập nhật danh sách sản phẩm
      await fetchProducts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong updateProduct:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Xóa sản phẩm
  async function deleteProduct(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.deleteProduct(id)
      // Cập nhật danh sách sản phẩm
      await fetchProducts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong deleteProduct:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi xóa sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Lấy danh sách danh mục
  async function fetchCategories(page = 0, size = 100, name = '') {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.getCategories(page, size, name)
      categories.value = response.data.content || []
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchCategories:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Tạo danh mục mới
  async function createCategory(categoryData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.createCategory(categoryData)
      // Cập nhật danh sách danh mục
      await fetchCategories()
      return response.data
    } catch (err) {
      console.error('Lỗi trong createCategory:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tạo danh mục mới'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật danh mục
  async function updateCategory(id, categoryData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.updateCategory(id, categoryData)
      // Cập nhật danh sách danh mục
      await fetchCategories()
      return response.data
    } catch (err) {
      console.error('Lỗi trong updateCategory:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Xóa danh mục
  async function deleteCategory(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.deleteCategory(id)
      // Cập nhật danh sách danh mục
      await fetchCategories()
      return response.data
    } catch (err) {
      console.error('Lỗi trong deleteCategory:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi xóa danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Lấy danh sách kích cỡ sản phẩm
  async function fetchProductSizes() {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.getProductSizes()
      productSizes.value = response.data.content || []
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchProductSizes:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách kích cỡ sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Thêm giá cho sản phẩm
  async function addProductPrice(productId, prices) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.addProductPrice(productId, prices)
      return response.data
    } catch (err) {
      console.error('Lỗi trong addProductPrice:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi thêm giá sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật giá sản phẩm
  async function updateProductPrice(productId, prices) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.updateProductPrice(productId, prices)
      return response.data
    } catch (err) {
      console.error('Lỗi trong updateProductPrice:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật giá sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Xóa giá sản phẩm theo kích cỡ
  async function deleteProductPrice(productId, sizeId) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.deleteProductPrice(productId, sizeId)
      return response.data
    } catch (err) {
      console.error('Lỗi trong deleteProductPrice:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi xóa giá sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    // State
    products,
    totalProducts,
    loading,
    error,
    currentPage,
    pageSize,
    categories,
    productSizes,
    
    // Getters
    totalPages,
    
    // Actions
    fetchProducts,
    fetchProductById,
    createProduct,
    updateProduct,
    deleteProduct,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    fetchProductSizes,
    addProductPrice,
    updateProductPrice,
    deleteProductPrice
  }
}) 