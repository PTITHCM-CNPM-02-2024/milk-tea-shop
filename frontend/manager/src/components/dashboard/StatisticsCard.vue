<template>
  <v-card class="statistics-card">
    <v-card-text>
      <div class="d-flex">
        <!-- Icon -->
        <v-avatar :color="iconBgColor" size="48" class="mr-4">
          <v-icon :color="iconColor" :icon="icon" size="24"></v-icon>
        </v-avatar>
        
        <!-- Content -->
        <div>
          <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ title }}</div>
          <div class="text-h5 font-weight-bold mb-1">{{ value }}</div>
          
          <!-- Growth indicator -->
          <div v-if="growth !== undefined" class="d-flex align-center">
            <v-icon
              :icon="growth >= 0 ? 'mdi-chevron-up' : 'mdi-chevron-down'"
              :color="growth >= 0 ? 'success' : 'error'"
              size="small"
              class="mr-1"
            ></v-icon>
            <span
              :class="growth >= 0 ? 'text-success' : 'text-error'"
              class="text-caption font-weight-medium"
            >
              {{ Math.abs(growth) }}%
            </span>
            <span class="text-caption text-medium-emphasis ml-1">vs last period</span>
          </div>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { defineProps, computed } from 'vue'

const props = defineProps({
  icon: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  growth: {
    type: Number,
    default: undefined
  },
  color: {
    type: String,
    default: 'primary'
  }
})

// Tính toán màu nền và màu icon dựa trên prop color
const iconBgColor = computed(() => {
  // Tạo màu nền nhạt cho icon
  return `${props.color}-lighten-4`
})

const iconColor = computed(() => {
  return props.color
})
</script>

<style scoped>
.statistics-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.statistics-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1) !important;
}
</style> 