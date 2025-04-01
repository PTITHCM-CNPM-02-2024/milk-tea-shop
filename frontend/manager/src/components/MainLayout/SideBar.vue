<script setup>
import { ref } from 'vue'
import {
  HomeIcon,
  UserGroupIcon,
  CubeIcon,
  ShoppingCartIcon,
  UsersIcon,
  TableCellsIcon,
  BuildingStorefrontIcon,
  TagIcon,
  ViewfinderCircleIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from '@heroicons/vue/24/outline'

// set bien de thu nho sidebar  + darkmode
const darkMode = ref(false)
const collapsed = ref(false)
const selectedSection = ref('Dashboard')

const menuItems = [
  { name: 'Account Management', path: '/accounts', icon: UserGroupIcon },
  { name: 'Products & Categories', path: '/products', icon: CubeIcon },
  { name: 'Orders & Payments', path: '/orders', icon: ShoppingCartIcon },
  { name: 'Customers & Members', path: '/customers', icon: UsersIcon },
  { name: 'Areas & Tables', path: '/areas', icon: TableCellsIcon },
  { name: 'Store Management', path: '/store', icon: BuildingStorefrontIcon },
  { name: 'Discounts & Promotions', path: '/discounts', icon: TagIcon },
]
// dao nguoc gia tri collapsed
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}
</script>

<template>
  <div
    class="flex h-screen transition-all"
    :class="darkMode ? 'bg-[#dde2fa] text-white' : 'bg-[#f8f8f8] text-[#060c12]'"
  >
    <!-- Sidebar -->
    <div
      class="sidebar flex flex-col border-r transition-all duration-300"
      :class="[
        collapsed ? 'w-16' : 'w-56',
        darkMode ? 'bg-[#191b25] border-[#353535]' : 'bg-white border-[#f3f4f6]',
      ]"
    >
      <!-- Logo + Toggle -->
      <div class="flex items-center justify-between px-4 py-4">
        <div class="logo-container flex items-center gap-2">
          <ViewfinderCircleIcon class="w-10 h-10 text-orange-500" />
          <span v-if="!collapsed" class="text-xl font-bold">Milk tea</span>
        </div>
        <button
          @click="toggleSidebar"
          class="p-2 rounded-full z-50 hover:bg-gray-200 dark:hover:bg-gray-700"
        >
          <ChevronLeftIcon v-if="!collapsed" class="w-6 h-6" />
          <ChevronRightIcon v-else class="w-6 h-6" />
        </button>
      </div>

      <!-- Menu Items -->
      <nav class="flex flex-col items-center py-4 space-y-2">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="relative group flex items-center gap-3 p-3 rounded-lg transition-all w-full"
          :class="[
            selectedSection === item.name
              ? 'bg-orange-500 text-white'
              : 'text-gray dark:text-black-900',
            'hover:bg-orange-500 hover:text-white',
          ]"
        >
          <component :is="item.icon" class="w-6 h-6" />
          <span v-if="!collapsed" class="text-sm font-medium">
            {{ item.name }}
          </span>
          <!-- Tooltip khi sidebar thu gọn -->
          <span
            v-else
            class="absolute left-14 bg-gray-800 text-white text-xs px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity"
          >
            {{ item.name }}
          </span>
        </router-link>
      </nav>
      <!-- Nút bật/tắt Dark Mode -->
      <div class="flex items-center justify-center mt-4">
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" v-model="darkMode" class="sr-only peer" />
          <div
            class="w-9 h-4 bg-gray-300 rounded-full peer-checked:bg-orange-500 transition-all relative"
          >
            <div
              class="absolute left-1 top-1 w-2 h-2 bg-white rounded-full shadow-md transform peer-checked:translate-x-6 transition-all"
            ></div>
          </div>
        </label>
      </div>
    </div>
  </div>
</template>

<style>
body {
  font-family: 'Inter', sans-serif;
}
</style>
