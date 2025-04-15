import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import ProductService from '../services/product.service';
import CategoryService from '../services/category.service';
import { useCategoryStore } from './categoryStore';

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref([]);
  const allProducts = ref([]); // Lưu trữ tất cả sản phẩm đã tải
  const currentProductDetail = ref(null);
  const loading = ref(false);
  const error = ref(null);
  
  // Computed
  const filteredProducts = computed(() => {
    const categoryStore = useCategoryStore();
    const selectedCategory = categoryStore.selectedCategory;
    
    // Nếu không có danh mục được chọn hoặc chọn "Tất cả", hiển thị tất cả sản phẩm
    if (!selectedCategory || selectedCategory === 'all' || (typeof selectedCategory === 'object' && selectedCategory.id === 'all')) {
      return products.value;
    }
    
    // Xử lý trường hợp danh mục "Khác" (hiển thị sản phẩm có catId là null)
    if (selectedCategory === 'null' || (typeof selectedCategory === 'object' && selectedCategory.id === 'null')) {
      return products.value.filter(product => !product.catId);
    }
    
    // Lọc sản phẩm theo danh mục đã chọn
    const categoryId = typeof selectedCategory === 'object' ? selectedCategory.id : selectedCategory;
    
    return products.value.filter(product => {
      return product.catId === categoryId || (product.catId && product.catId.toString() === categoryId.toString());
    });
  });
  
  // Actions
  async function fetchProductsByCategory(categoryId, page = 0, size = 100, availableOrdered = true) {
    // Không gọi API nữa mà chỉ lọc từ allProducts đã tải trước đó
    loading.value = true;
    
    try {
      if (categoryId === 'all') {
        // Nếu là danh mục "Tất cả", hiển thị tất cả sản phẩm
        products.value = [...allProducts.value];
      } else if (categoryId === 'null') {
        // Nếu là danh mục "Khác", lọc sản phẩm không có danh mục
        products.value = allProducts.value.filter(product => !product.catId);
      } else {
        // Lọc sản phẩm theo danh mục đã chọn
        products.value = allProducts.value.filter(product => {
          return product.catId === categoryId || 
                 (product.catId && categoryId && product.catId.toString() === categoryId.toString());
        });
      }
    } catch (err) {
      console.error('Lỗi khi lọc sản phẩm theo danh mục:', err);
      error.value = err.message || 'Không thể lọc sản phẩm theo danh mục';
    } finally {
      loading.value = false;
    }
  }
  
  async function fetchAllProducts(page = 0, size = 100) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await ProductService.getAvailableProducts(true, page, size);
      
      if (response && response.data) {
        if (Array.isArray(response.data)) {
          allProducts.value = response.data;
          products.value = response.data;
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // Trường hợp API trả về Page object
          allProducts.value = response.data.content;
          products.value = response.data.content;
        } else {
          allProducts.value = [];
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
  
  async function fetchToppings() {
    // Tải topping từ API khi cần - topping thuộc danh mục có id là 1
    console.log('Đang tải topping từ danh mục 1...');
    try {
      const response = await CategoryService.getCategoryProducts(1, true);
      
      console.log('Kết quả API topping:', response);
      
      if (response && response.data) {
        const toppingList = Array.isArray(response.data) 
          ? response.data 
          : (response.data.content && Array.isArray(response.data.content) ? response.data.content : []);
          
        console.log('Danh sách topping đã tải:', toppingList);
        return toppingList;
      }
      console.log('Không có topping được tìm thấy');
      return [];
    } catch (err) {
      console.error('Lỗi khi tải topping:', err);
      return [];
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
    allProducts,
    currentProductDetail,
    loading,
    error,
    
    // Computed
    filteredProducts,
    
    // Actions
    fetchProductsByCategory,
    fetchAllProducts,
    fetchProductDetail,
    fetchToppings,
    handleCategoryChange
  };
}); 