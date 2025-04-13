<template>
  <dashboard-layout>
    <v-card>
      <router-view></router-view>
    </v-card>
  </dashboard-layout>
</template>

<script setup>
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'
</script>

<style scoped>
.border-b {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}
</style> 