<template>
  <div class="category-container">
    <div class="category-wrapper">
      <div class="category-cards-container px-4 py-3">
        <div
          v-for="category in uniqueCategories"
          :key="category.id || category"
          @click="handleCategoryChange(category.id || category)"
          :class="[
            'category-card', 
            (category.id || category) == selectedCategoryIndex ? 'category-card-active' : ''
          ]"
          :title="getCategoryDisplayName(category)"
        >
          <div class="category-image-wrapper">
            <img v-if="getCategoryImage(category)" :src="getCategoryImage(category)" class="category-image" />
            <v-icon v-else :icon="getCategoryIcon(category)" size="x-large"></v-icon>
          </div>
          <div class="category-name">
            {{ getCategoryDisplayName(category) }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch, onMounted, computed } from 'vue';

const props = defineProps({
  categories: {
    type: Array,
    required: true
  },
  selectedCategory: {
    type: [String, Number, Object],
    required: true
  },
  products: {
    type: Array,
    default: () => []
  },
  allProducts: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['select-category']);

const selectedCategoryIndex = ref(null);

// Tạo danh sách danh mục duy nhất dựa trên ID
const uniqueCategories = computed(() => {
  // Tạo map để lưu trữ danh mục theo ID
  const categoryMap = new Map();

  // Thêm danh mục vào Map (tự động ghi đè nếu ID trùng)
  props.categories.forEach(category => {
    const categoryId = typeof category === 'object' ? category.id : category;
    categoryMap.set(categoryId, category);
  });

  // Debug
  console.log('Products passed to CategoryList:', props.products);
  console.log('First product image URL:', props.products && props.products.length > 0 ? (props.products[0].image_url || props.products[0].imageUrl || 'No image URL') : 'No products');

  // Chuyển đổi Map thành mảng
  return Array.from(categoryMap.values());
});

// Cập nhật giá trị selectedCategoryIndex dựa trên selectedCategory
const updateSelectedIndex = () => {
  if (props.selectedCategory === null || props.selectedCategory === undefined) {
    selectedCategoryIndex.value = null;
    return;
  }

  if (typeof props.selectedCategory === 'object') {
    selectedCategoryIndex.value = props.selectedCategory.id;
  } else {
    selectedCategoryIndex.value = props.selectedCategory;
  }
};

function getCategoryDisplayName(category) {
  // Xử lý trường hợp category là đối tượng
  if (typeof category === 'object' && category !== null) {
    if (category.id === 'all') return 'Tất cả';
    if (category.id === 'null' || category.id === null) return 'Khác';
    return category.name || 'Không tên';
  }

  // Xử lý trường hợp category là string hoặc giá trị khác
  if (category === 'all') return 'Tất cả';
  if (category === 'null' || category === null) return 'Khác';
  return category;
}

function getCategoryImage(category) {
  // Ưu tiên sử dụng allProducts (tất cả sản phẩm) nếu có
  const productsSource = props.allProducts && props.allProducts.length > 0 ? props.allProducts : props.products;
  
  // Nếu không có sản phẩm, trả về null
  if (!productsSource || productsSource.length === 0) return null;
  
  const categoryName = getCategoryDisplayName(category).toLowerCase();
  const categoryId = typeof category === 'object' ? category.id : category;
  
  // Tất cả là danh mục đặc biệt - lấy hình ảnh sản phẩm đầu tiên
  if (categoryName === 'tất cả') {
    const firstProduct = productsSource[0];
    // Kiểm tra cả hai trường hợp image_url và imageUrl
    return firstProduct && (firstProduct.image_url || firstProduct.imageUrl) 
      ? (firstProduct.image_url || firstProduct.imageUrl) 
      : null;
  }

  // Xử lý danh mục "Khác"
  if (categoryId === 'null' || categoryId === null) {
    // Tìm sản phẩm đầu tiên không có danh mục (catId là null hoặc undefined)
    const productsWithoutCategory = productsSource.filter(product => !product.catId);
    
    if (productsWithoutCategory.length > 0) {
      const product = productsWithoutCategory[0];
      return product.image_url || product.imageUrl || null;
    }
    return null;
  }
  
  // Tìm sản phẩm đầu tiên thuộc danh mục này (dựa vào catId)
  const productsInCategory = productsSource.filter(product => {
    return product.catId === categoryId || 
           (product.catId && categoryId && product.catId.toString() === categoryId.toString());
  });
  
  if (productsInCategory.length > 0) {
    const product = productsInCategory[0];
    return product.image_url || product.imageUrl || null;
  }
  
  return null;
}

function getCategoryIcon(category) {
  // Trả về icon phù hợp dựa vào tên category (dùng làm backup khi không có ảnh)
  const name = getCategoryDisplayName(category).toLowerCase();
  
  if (name === 'tất cả') return 'mdi-view-grid';
  if (name.includes('trà') || name.includes('tea')) return 'mdi-tea';
  if (name.includes('cà phê') || name.includes('coffee')) return 'mdi-coffee';
  if (name.includes('sữa') || name.includes('milk')) return 'mdi-cup';
  if (name.includes('đá xay') || name.includes('smoothie')) return 'mdi-blender';
  if (name.includes('trân châu') || name.includes('bubble')) return 'mdi-circle-small';
  if (name.includes('topping')) return 'mdi-food-variant';
  
  // Icon mặc định
  return 'mdi-glass-cocktail';
}

function handleCategoryChange(categoryId) {
  // Tìm category object từ categoryId
  const selectedCategory = uniqueCategories.value.find(c =>
      (typeof c === 'object' ? c.id === categoryId : c === categoryId)
  );

  selectedCategoryIndex.value = categoryId;
  emit('select-category', selectedCategory || categoryId);
}

// Watch for prop changes to keep sync
watch(() => props.selectedCategory, () => {
  updateSelectedIndex();
}, { immediate: true });

// Set initial selected index
onMounted(() => {
  updateSelectedIndex();
});
</script>

<style scoped>
.category-container {
  background: linear-gradient(to right, var(--v-theme-background), var(--v-theme-surface));
  border-bottom: 1px solid rgba(var(--v-border-opacity, 1), 0.1);
  position: relative;
  box-shadow: 0 2px 6px rgba(var(--v-shadow-opacity, 0), 0.1);
  height: 150px; /* Chiều cao cố định cho container */
}

.category-wrapper {
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(var(--v-border-opacity, 1), 0.3) transparent;
  height: 100%;
  -webkit-overflow-scrolling: touch;
}

.category-wrapper::-webkit-scrollbar {
  height: 6px;
}

.category-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.category-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(var(--v-border-opacity, 1), 0.3);
  border-radius: 20px;
}

.category-cards-container {
  display: flex;
  gap: 16px;
  padding-bottom: 8px;
  height: 100%;
  align-items: center;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 200px;
  height: 120px;
  min-width: 100px;
  flex: 0 0 auto;
  background-color: var(--v-theme-surface);
  border-radius: 12px;
  box-shadow: 0 3px 6px rgba(var(--v-shadow-opacity, 0), 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(var(--v-border-opacity, 1), 0.1);
  padding: 12px 8px;
  user-select: none;
  position: relative;
}

.category-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(var(--v-shadow-opacity, 0), 0.15);
  border-color: var(--v-theme-primary);
}

.category-card-active {
  background: linear-gradient(135deg, var(--v-theme-primary), var(--v-theme-info));
  color: white !important; /* Đảm bảo màu chữ sáng */
  border: none;
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(var(--v-theme-primary-rgb, 33, 150, 243), 0.3);
}

.category-card-active .v-icon {
  color: white !important;
}

.category-image-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  margin-bottom: 8px;
  background-color: rgba(var(--v-theme-primary-rgb, 33, 150, 243), 0.1);
  overflow: hidden;
  flex-shrink: 0;
}

.category-card-active .category-image-wrapper {
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.5);
}

.category-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.category-name {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.2;
  max-height: 2.4em;
  white-space: normal;
  color: var(--v-theme-on-surface);
}

.category-card-active .category-name {
  font-weight: 600;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2); /* Thêm text shadow để dễ đọc hơn khi nền là màu */
  color: white;
}

/* Thêm tooltip hiển thị tên đầy đủ khi hover */
.category-card:hover::after {
  content: attr(data-title);
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  white-space: nowrap;
  z-index: 10;
  display: none; /* Tạm thời ẩn tooltip này */
}

.category-price {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--v-theme-warning);
  margin-top: 4px;
  background-color: rgba(var(--v-theme-warning-rgb, 255, 107, 0), 0.1);
  padding: 2px 6px;
  border-radius: 12px;
}

.category-card-active .category-price {
  color: white;
  background-color: rgba(255, 255, 255, 0.3);
}
</style>