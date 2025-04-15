<template>
  <router-view></router-view>
</template>

<script setup>
// Không cần import DashboardLayout
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