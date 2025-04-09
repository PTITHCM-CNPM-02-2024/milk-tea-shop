import { defineStore } from 'pinia';
import { ref } from 'vue';
import CategoryService from '../services/category.service';

export const useCategoryStore = defineStore('category', () => {
  // State
  const categories = ref([]);
  const selectedCategory = ref({ id: 'all', name: 'Tất cả' });
  const loading = ref(false);
  const error = ref(null);
  
  // Actions
  async function fetchCategories(page = 0, size = 50) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await CategoryService.getAllCategories(page, size);
      
      if (response && response.data) {
        let categoriesList = [];
        
        if (Array.isArray(response.data)) {
          categoriesList = response.data;
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // Trường hợp API trả về Page object
          categoriesList = response.data.content;
        } else {
          console.error('Cấu trúc dữ liệu danh mục không đúng:', response.data);
        }
        
        // Lọc bỏ danh mục topping (nếu có thể xác định bằng tên)
        categoriesList = categoriesList.filter(cat => 
          !cat.name || !cat.name.toLowerCase().includes('topping')
        );
        
        // Thêm danh mục đặc biệt
        categories.value = [
          { id: 'all', name: 'Tất cả' },
          ...categoriesList,
          { id: 'null', name: 'Không danh mục' } // Thêm danh mục đặc biệt cho sản phẩm không thuộc danh mục nào
        ];
        
        // Nếu chưa có danh mục nào được chọn, chọn danh mục đầu tiên
        if (!selectedCategory.value || selectedCategory.value.id === 'all') {
          selectedCategory.value = categories.value[0];
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
    
    // Actions
    fetchCategories,
    selectCategory
  };
}); 