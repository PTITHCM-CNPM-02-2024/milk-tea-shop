<template>
  <dashboard-layout>
    <!-- <v-card class="mb-4">
      <v-card-text>
        <div class="d-flex flex-column flex-md-row justify-space-between align-md-center">
          <div>
            <h1 class="text-h4 font-weight-bold">Quản lý tài khoản</h1>
            <p class="text-medium-emphasis mt-1">
              Quản lý tài khoản người dùng và vai trò trong hệ thống
            </p>
          </div>
        </div>
      </v-card-text>
    </v-card> -->
    
    <v-card>
      <v-tabs
        v-model="activeTab"
        color="primary"
        align-tabs="start"
        class="border-b"
      >
        <v-tab :value="0" to="/account/list">
          <v-icon start>mdi-account-multiple</v-icon>
          Danh sách tài khoản
        </v-tab>
        <v-tab :value="1" to="/account/roles">
          <v-icon start>mdi-shield-account</v-icon>
          Vai trò người dùng
        </v-tab>
      </v-tabs>
      
      <router-view></router-view>
    </v-card>
  </dashboard-layout>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

const route = useRoute()
const activeTab = ref(0)

// Theo dõi thay đổi route để cập nhật tab
watch(() => route.path, (newPath) => {
  if (newPath.includes('/account/roles')) {
    activeTab.value = 1
  } else {
    activeTab.value = 0
  }
}, { immediate: true })
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