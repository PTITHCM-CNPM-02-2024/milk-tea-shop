<template>
  <div class="category-container pa-3">
    <v-chip-group
        v-model="selectedCategoryIndex"
        mandatory
        @update:modelValue="handleCategoryChange"
    >
      <v-chip
          v-for="category in uniqueCategories"
          :key="category.id || category"
          :value="category.id || category"
          filter
          variant="elevated"
      >
        {{ getCategoryDisplayName(category) }}
      </v-chip>
    </v-chip-group>
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
    type: [String, Number, Object], // Thêm Object vào danh sách kiểu dữ liệu
    required: true
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
    if (category.id === 'all' || category === 'all') return 'Tất cả';
    return category.name || 'Không tên';
  }

  // Xử lý trường hợp category là string hoặc giá trị khác
  if (category === 'all') return 'Tất cả';
  return category;
}

function handleCategoryChange(categoryId) {
  // Tìm category object từ categoryId
  const selectedCategory = uniqueCategories.value.find(c =>
      (typeof c === 'object' ? c.id == categoryId : c == categoryId)
  );

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
  background-color: #f8f9fa;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
  overflow-x: auto;
}
</style>