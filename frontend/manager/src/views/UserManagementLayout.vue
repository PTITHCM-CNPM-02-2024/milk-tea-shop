<template>
  <dashboard-layout>
    <router-view></router-view>
  </dashboard-layout>
</template>

<script setup>
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'
</script>

<style scoped>
.border-b {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

:deep(.v-tab) {
  min-width: 150px;
  font-weight: 500;
}
</style> 