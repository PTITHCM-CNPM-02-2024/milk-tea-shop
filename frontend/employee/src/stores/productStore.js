import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import ProductService from '../services/product.service';
import CategoryService from '../services/category.service';
import { useCategoryStore } from './categoryStore';

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref([]);
  const currentProductDetail = ref(null);
  const loading = ref(false);
  const error = ref(null);
  
  // Computed
  const filteredProducts = computed(() => {
    const categoryStore = useCategoryStore();
    const selectedCategory = categoryStore.selectedCategory;
    
    if (!selectedCategory || selectedCategory.id === 'all') {
      return products.value;
    }
    
    // Lọc sản phẩm theo danh mục đã chọn, trừ topping
    return products.value.filter(product => {
      // Kiểm tra và loại bỏ topping
      const categoryName = product.category && typeof product.category === 'object' 
        ? product.category.name?.toLowerCase() 
        : '';
      
      const productName = product.name?.toLowerCase() || '';
      
      // Kiểm tra nếu là topping
      if (categoryName.includes('topping') || productName.includes('topping')) {
        return false;
      }
      
      return true;
    });
  });
  
  // Actions
  async function fetchProductsByCategory(categoryId, page = 0, size = 100, availableOrdered = true) {
    if (categoryId === 'all') {
      // Nếu là danh mục "Tất cả", tải tất cả sản phẩm
      return await fetchAllProducts(page, size);
    }
    
    loading.value = true;
    error.value = null;
    
    try {
      const response = await CategoryService.getCategoryProducts(categoryId, availableOrdered, page, size);
      
      if (response && response.data) {
        console.log(`DEBUG: Sản phẩm theo categoryId=${categoryId}:`, response.data);
        
        if (Array.isArray(response.data)) {
          products.value = response.data;
          // Debug một sản phẩm đầu tiên để kiểm tra cấu trúc
          if (response.data.length > 0) {
            console.log('DEBUG: Cấu trúc một sản phẩm:', response.data[0]);
          }
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // Trường hợp API trả về Page object
          products.value = response.data.content;
          // Debug một sản phẩm đầu tiên để kiểm tra cấu trúc
          if (response.data.content.length > 0) {
            console.log('DEBUG: Cấu trúc một sản phẩm (từ Page):', response.data.content[0]);
          }
        } else {
          products.value = [];
          console.error('Cấu trúc dữ liệu sản phẩm không đúng:', response.data);
        }
      }
    } catch (err) {
      console.error('Lỗi khi tải sản phẩm theo danh mục:', err);
      error.value = err.message || 'Không thể tải sản phẩm theo danh mục';
    } finally {
      loading.value = false;
    }
  }
  
  async function fetchAllProducts(page = 0, size = 100) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await ProductService.getAvailableProducts(page, size);
      
      if (response && response.data) {
        if (Array.isArray(response.data)) {
          products.value = response.data;
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // Trường hợp API trả về Page object
          products.value = response.data.content;
        } else {
          products.value = [];
          console.error('Cấu trúc dữ liệu sản phẩm không đúng:', response.data);
        }
      }
    } catch (err) {
      console.error('Lỗi khi tải tất cả sản phẩm:', err);
      error.value = err.message || 'Không thể tải sản phẩm';
    } finally {
      loading.value = false;
    }
  }
  
  async function fetchProductDetail(productId) {
    if (!productId) return null;
    
    try {
      const response = await ProductService.getProductById(productId);
      
      if (response && response.data) {
        currentProductDetail.value = response.data;
        return response.data;
      }
      return null;
    } catch (err) {
      console.error('Lỗi khi tải chi tiết sản phẩm:', err);
      error.value = err.message || 'Không thể tải chi tiết sản phẩm';
      return null;
    }
  }
  
  // Xử lý khi danh mục thay đổi
  function handleCategoryChange(category) {
    if (!category) return;
    
    const categoryId = typeof category === 'object' ? category.id : category;
    fetchProductsByCategory(categoryId);
  }
  
  return {
    // State
    products,
    currentProductDetail,
    loading,
    error,
    
    // Computed
    filteredProducts,
    
    // Actions
    fetchProductsByCategory,
    fetchAllProducts,
    fetchProductDetail,
    handleCategoryChange
  };
}); 