import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import CategoryService from '../services/category.service';

export const useCategoryStore = defineStore('category', () => {
  // State
  const categories = ref([]);
  const selectedCategory = ref('all');
  const loading = ref(false);
  const error = ref(null);
  
  // Computed
  const allCategories = computed(() => {
    // Luôn thêm danh mục "Tất cả" vào đầu danh sách
    const allCategoriesOption = { id: 'all', name: 'Tất cả' };
    
    // Thêm danh mục "Khác" để hiển thị sản phẩm không có danh mục
    const nullCategoryOption = { id: 'null', name: 'Khác' };
    
    // Lọc bỏ danh mục topping từ danh sách danh mục
    const filteredCategories = categories.value.filter(category => {
      const name = category.name?.toLowerCase() || '';
      return !name.includes('topping');
    });
    
    // Gộp danh sách (không thêm topping)
    return [allCategoriesOption, ...filteredCategories, nullCategoryOption];
  });
  
  // Actions
  async function fetchCategories(page = 0, size = 100) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await CategoryService.getAllCategories(page, size);
      
      if (response && response.data) {
        if (Array.isArray(response.data)) {
          categories.value = response.data;
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // Xử lý trường hợp API trả về Page object
          categories.value = response.data.content;
        } else {
          categories.value = [];
          console.error('Cấu trúc dữ liệu danh mục không đúng:', response.data);
        }
      }
    } catch (err) {
      console.error('Lỗi khi tải danh mục:', err);
      error.value = err.message || 'Không thể tải danh mục';
    } finally {
      loading.value = false;
    }
  }
  
  function selectCategory(category) {
    selectedCategory.value = category;
  }
  
  return {
    // State
    categories,
    selectedCategory,
    loading,
    error,
    
    // Computed
    allCategories,
    
    // Actions
    fetchCategories,
    selectCategory
  };
}); 