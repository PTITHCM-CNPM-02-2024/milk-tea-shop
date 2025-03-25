<template>
  <div class="category-container pa-3">
    <v-chip-group
        v-model="selectedCategoryIndex"
        mandatory
        @update:modelValue="handleCategoryChange"
    >
      <v-chip
          v-for="category in categories"
          :key="category"
          :value="category"
          filter
          variant="elevated"
      >
        {{ getCategoryDisplayName(category) }}
      </v-chip>
    </v-chip-group>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
  categories: {
    type: Array,
    required: true
  },
  selectedCategory: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['select-category']);

const selectedCategoryIndex = ref(props.selectedCategory);

function getCategoryDisplayName(category) {
  if (category === 'all') return 'Tất cả';
  return category;
}

function handleCategoryChange(category) {
  emit('select-category', category);
}

// Watch for prop changes to keep sync
watch(() => props.selectedCategory, (newValue) => {
  selectedCategoryIndex.value = newValue;
});
</script>

<style scoped>
.category-container {
  background-color: #f8f9fa;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
  overflow-x: auto;
}
</style>