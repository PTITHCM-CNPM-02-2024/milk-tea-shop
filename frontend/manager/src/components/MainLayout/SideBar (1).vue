<script setup>
import { ref, provide } from 'vue'
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
const darkMode = ref(true)
const collapsed = ref(false)
const selectedSection = ref('Dashboard')

const menuItems = [
  { name: 'Account Management', path: '/accounts', icon: UserGroupIcon },
  {
    name: 'Products & Categories',
    path: '/products',
    icon: CubeIcon,
    subItems: [
      { name: 'Danh sách danh mục', path: '/products/categories' },
      { name: 'Danh sách sản phẩm', path: '/products/product-list' },
      { name: 'Chi tiết sản phẩm', path: '/products/product-detail' }, // Có thể thêm logic để hiển thị chi tiết dựa trên ID
      // { name: 'Quản lý giá theo kích thước', path: '/products/size-price' },
      // { name: 'Quản lý giảm giá', path: '/discounts' },
    ],
  },
  {
    name: 'Orders & Payments',
    path: '/orders',
    icon: ShoppingCartIcon,
    subItems: [
      { name: 'Orders', path: '/orders/list' },
      { name: 'Payments', path: '/orders/payment' },
    ],
  },
  {
    name: 'Customers & Members',
    path: '/customers',
    icon: UsersIcon,
    subItems: [
      { name: 'Customers', path: '/customers/list' },
      { name: 'Membership', path: '/customers/membership' },
    ],
  },

  {
    name: 'Areas & Tables',
    path: '/areas',
    icon: TableCellsIcon,
    subItems:[
      {name:'Areas', path: '/areas/list'},
      {name:'Tables', path: '/areas/tables'},
  ]
  },
  {
    name: 'Store Management',
    path: '/store',
    icon: BuildingStorefrontIcon,
  },
  {
    name: 'Discounts & Promotions',
    path: '/discounts',
    icon: TagIcon,
    subItems:[
      {name:'Discounts', path: '/discount/list'},
      {name:'Promotions', path: '/discount/promotion'}
    ]
  },
]
// dao nguoc gia tri collapsed
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}
const expandedMenus = ref({}) // Theo dõi trạng thái mở của từng menu

const toggleMenu = (menuName) => {
  expandedMenus.value[menuName] = !expandedMenus.value[menuName]
}
provide('collapsed', collapsed)
provide('toggleSidebar', toggleSidebar)
</script>

<template>
  <div
    class="flex h-screen transition-all"
    :class="darkMode ? 'bg-[#dde2fa] text-white' : 'bg-[#f8f8f8] text-[#060c12]'"
  >
    <!-- Sidebar -->
    <div
      class="sidebar fixed h-full flex flex-col overflow-y-auto border-r transition-all duration-300"
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
        <template v-for="item in menuItems" :key="item.path">
          <!-- Nếu không có submenu -->
          <router-link
            v-if="!item.subItems"
            :to="item.path"
            @click="selectedSection = item.name"
            class="relative group flex items-center gap-3 p-3 rounded-lg transition-all w-full"
            :class="[
              selectedSection === item.name
                ? 'bg-orange-500 text-white'
                : 'text-gray dark:text-black-900',
              'hover:bg-orange-500 hover:text-white',
            ]"
          >
            <component :is="item.icon" class="w-6 h-6" />
            <span v-if="!collapsed" class="text-sm font-medium">{{ item.name }}</span>
          </router-link>

          <!-- Nếu có submenu -->
          <!-- Nếu có submenu -->
          <div v-else class="w-full">
            <!-- Mục chính có submenu -->
            <div class="relative w-full">
              <router-link
                :to="item.path"
                @click="selectedSection = item.name"
                class="flex items-center gap-3 p-3 rounded-lg transition-all w-full"
                :class="[
                  selectedSection === item.name
                    ? 'bg-orange-500 text-white'
                    : 'text-gray dark:text-black-900',
                  'hover:bg-orange-500 hover:text-white',
                ]"
              >
                <component :is="item.icon" class="w-6 h-6 min-w-6 min-h-6" />
                <span v-if="!collapsed" class="text-sm font-medium">{{ item.name }}</span>
              </router-link>
              <!-- Nút toggle submenu -->
              <button
                v-if="!collapsed"
                @click="toggleMenu(item.name)"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 p-1"
              >
                <ChevronRightIcon
                  v-if="!expandedMenus[item.name]"
                  class="w-5 h-5 transition-transform"
                />
                <ChevronLeftIcon v-else class="w-5 h-5 transition-transform" />
              </button>
            </div>

            <!-- Danh sách submenu -->
            <div v-if="expandedMenus[item.name] && !collapsed" class="ml-6 space-y-1">
              <router-link
                v-for="sub in item.subItems"
                :key="sub.path"
                :to="sub.path"
                @click="selectedSection = sub.name"
                class="flex items-center gap-3 p-2 pl-6 rounded-lg text-sm transition-all"
                :class="[
                  selectedSection === sub.name
                    ? 'bg-orange-500 text-white'
                    : 'text-gray dark:text-gray-500',
                  'hover:bg-orange-400 hover:text-white',
                ]"
              >
                • {{ sub.name }}
              </router-link>
            </div>
          </div>
        </template>
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
.sidebar {
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE 10+ */
}

.sidebar::-webkit-scrollbar {
  display: none;
}
</style>
