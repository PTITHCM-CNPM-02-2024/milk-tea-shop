<script setup>
import { useTheme } from 'vuetify'
import { onMounted, ref, watch, computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { useDisplay } from 'vuetify'
import { useRoute, useRouter } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'
// import Login from './components/MainLayout/Login.vue'
// import { HomeIcon, Cog6ToothIcon, ShoppingCartIcon, HeartIcon } from '@heroicons/vue/24/outline'
// import Posdasboard from './components/MainLayout/SideBar.vue'
//

const theme = useTheme()
const appStore = useAppStore()
const display = useDisplay()
const route = useRoute()

const rail = ref(false)
const darkMode = ref(false)  // Mặc định light mode để giống hình

const isMobile = computed(() => {
  return display.mdAndDown.value
})

onMounted(() => {
  // Khởi tạo theme từ store
  const isDarkMode = appStore.darkMode
  theme.global.name.value = isDarkMode ? 'dark' : 'light'
  darkMode.value = isDarkMode

  // Tự động thu gọn sidebar trên màn hình nhỏ
  rail.value = isMobile.value
})

// Theo dõi thay đổi kích thước màn hình
watch(isMobile, (newValue) => {
  if (newValue) {
    rail.value = true
  }
})

watch(darkMode, (newValue) => {
  theme.global.name.value = newValue ? 'dark' : 'light'
  appStore.setDarkMode(newValue)
})

const toggleRail = () => {
  rail.value = !rail.value
}

// Lấy tên route hiện tại để hiển thị trên header
const currentRouteName = computed(() => route.meta.title || route.name || 'Dashboard')

// Xác định layout nào sẽ được sử dụng
const layout = computed(() => {
  const layoutName = route.meta.layout || 'default'
  return layoutName === 'blank' ? BlankLayout : DefaultLayout
})
</script>

<template>
  <component :is="layout">
    <router-view />
  </component>
</template>

<style>
/* Global CSS */
html, body {
  margin: 0;
  padding: 0;
  font-family: 'Roboto', sans-serif;
}

.cursor-pointer {
  cursor: pointer;
}

/* Animation mượt mà khi chuyển trang */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease-in-out;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Style custom cho sidebar */
.sidebar-custom {
  font-family: 'Inter', 'Roboto', sans-serif;
  border-right: none !important;
}

.sidebar-header {
  margin-bottom: 8px;
  position: relative;
}

.sidebar-custom .v-list {
  padding: 0 12px;
}

/* Nút mở rộng sidebar */
.rail-expand-btn {
  opacity: 0.85;
  position: absolute;
  top: 12px;
  right: 8px;
  z-index: 2;
  background-color: rgba(0, 0, 0, 0.1);
}

.rail-expand-btn:hover {
  opacity: 1;
  background-color: rgba(0, 0, 0, 0.2);
}

.sidebar-item {
  border-radius: 10px !important;
  margin-bottom: 4px;
  color: white !important;
  font-weight: 400;
  min-height: 42px !important;
}

.sidebar-item .v-list-item__prepend > .v-icon {
  margin-inline-end: 14px;
}

.sidebar-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.sidebar-item-active {
  background-color: rgba(255, 255, 255, 0.18) !important;
  font-weight: 500 !important;
  color: white !important;
}

.sidebar-item-active .v-icon {
  color: white !important;
}

/* Sidebar group styling */
.v-list-group__items .sidebar-sub-item {
  padding-left: 12px;
  color: rgba(255, 255, 255, 0.85) !important;
  min-height: 38px !important;
  margin-bottom: 2px;
  font-size: 14px;
}

.sign-out-btn {
  text-transform: none !important;
  letter-spacing: 0 !important;
  font-weight: 500 !important;
  color: #8E6E4E !important;
}

.sign-out-btn .v-icon {
  color: #8E6E4E !important;
}

.close-btn {
  opacity: 0.7;
}

.close-btn:hover {
  opacity: 1;
}

/* Main content style */
.main-content {
  background-color: #F8F8F8;
}

.header-bar {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.page-title {
  background-color: white;
}

.search-field {
  max-width: 240px;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.3);
}

/* Chỉnh sửa style cho responsive */
@media (max-width: 959px) {
  .v-navigation-drawer {
    transform: translateX(0) !important;
  }
}
</style>
