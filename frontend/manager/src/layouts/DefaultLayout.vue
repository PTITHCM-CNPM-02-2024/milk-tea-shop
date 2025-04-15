<template>
  <v-app :theme="theme.global.name.value">
    <!-- Sidebar -->
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      app
      :elevation="2"
      :color="darkMode ? '#2F3349' : '#FFFFFF'"
      width="260"
      class="dashboard-sidebar"
    >
      <!-- Logo và Brand -->
      <div class="d-flex align-center justify-space-between py-3 px-4">
        <div class="d-flex align-center">
          <v-avatar color="primary" class="mr-3" size="32" v-if="!rail">
            <span class="text-h6 font-weight-bold">M</span>
          </v-avatar>
          <v-avatar color="primary" size="32" v-else>
            <span class="text-h6 font-weight-bold">M</span>
          </v-avatar>
          <span class="text-h6 font-weight-bold primary--text" v-if="!rail">MATERIO</span>
        </div>
        <v-btn
          v-if="!rail && !isMobile"
          icon="mdi-chevron-left"
          size="small"
          variant="text"
          @click="toggleRail"
        ></v-btn>
        <v-btn
          v-if="rail && !isMobile"
          icon="mdi-chevron-right"
          size="small"
          variant="text"
          @click="toggleRail"
        ></v-btn>
      </div>

      <v-divider></v-divider>

      <!-- Menu Categories -->
      <v-list nav density="compact" class="pa-2">
        <!-- Account Management with sub-items -->
        <v-list-group value="account">
          <template v-slot:activator="{ props }">
            <v-list-item
              v-bind="props"
              prepend-icon="mdi-account-cog-outline"
              title="Tài khoản"
              value="account"
              rounded="lg"
              class="mb-1"
            ></v-list-item>
          </template>

          <v-list-item
            title="Danh sách tài khoản"
            value="account-list"
            rounded="lg"
            prepend-icon="mdi-account-multiple"
            class="ms-2"
            to="/account/list"
            active-class="gradient-background text-white"
          ></v-list-item>

          <v-list-item
            title="Vai trò người dùng"
            value="account-roles"
            rounded="lg"
            prepend-icon="mdi-shield-account"
            class="ms-2"
            to="/account/roles"
            active-class="gradient-background text-white"
          ></v-list-item>
        </v-list-group>

        <!-- Người dùng -->
        <v-list-group value="users">
          <template v-slot:activator="{ props }">
            <v-list-item
              v-bind="props"
              prepend-icon="mdi-account-group-outline"
              title="Người dùng"
              value="users"
              rounded="lg"
              class="mb-1"
            ></v-list-item>
          </template>

          <v-list-item
            title="Nhân viên"
            value="employees"
            rounded="lg"
            prepend-icon="mdi-account-tie"
            class="ms-2"
            to="/users/employees"
            active-class="gradient-background text-white"
          ></v-list-item>

          <v-list-item
            title="Khách hàng & CT Thành viên"
            value="customers-membership"
            rounded="lg"
            prepend-icon="mdi-account-star"
            class="ms-2"
            to="/users/customers"
            active-class="gradient-background text-white"
          ></v-list-item>
        </v-list-group>

        <!-- Products & Categories -->
        <v-list-group value="products">
          <template v-slot:activator="{ props }">
            <v-list-item
              v-bind="props"
              prepend-icon="mdi-cube-outline"
              title="Sản phẩm"
              value="products"
              rounded="lg"
              class="mb-1"
            ></v-list-item>
          </template>

          <v-list-item
            title="Sản phẩm & Danh mục"
            value="product-and-categories"
            rounded="lg"
            prepend-icon="mdi-package-variant-closed"
            class="ms-2"
            to="/products/list"
            active-class="gradient-background text-white"
          ></v-list-item>

          <v-list-item
            title="Kích thước & Đơn vị tính"
            value="size-units"
            rounded="lg"
            prepend-icon="mdi-ruler"
            class="ms-2"
            to="/products/size-units"
            active-class="gradient-background text-white"
          ></v-list-item>
        </v-list-group>

        <!-- Orders & Payments -->
        <v-list-group value="orders-payments">
          <template v-slot:activator="{ props }">
            <v-list-item
              v-bind="props"
              prepend-icon="mdi-cart-outline"
              title="Đơn hàng & Thanh toán"
              value="orders-payments"
              rounded="lg"
              class="mb-1"
            ></v-list-item>
          </template>

          <v-list-item
            title="Đơn hàng"
            value="orders"
            rounded="lg"
            prepend-icon="mdi-clipboard-text-outline"
            class="ms-2"
            to="/orders"
            active-class="gradient-background text-white"
          ></v-list-item>

          <v-list-item
            title="Thanh toán & Phương thức"
            value="payments"
            rounded="lg"
            prepend-icon="mdi-credit-card-outline"
            class="ms-2"
            to="/payments"
            active-class="gradient-background text-white"
          ></v-list-item>
        </v-list-group>

        <!-- Areas & Tables -->
        <v-list-item
          prepend-icon="mdi-table-furniture"
          title="Khu vực & Bàn"
          value="areas"
          rounded="lg"
          to="/areas"
          class="mb-1"
        ></v-list-item>

        <!-- Discounts & Promotions -->
          <v-list-item
            title="Chương trình & Mã giảm giá"
            value="discounts"
            rounded="lg"
            prepend-icon="mdi-sale"
            to="/discounts"
            class="mb-1"
          ></v-list-item>


        <!-- Store Management -->
        <v-list-item
          prepend-icon="mdi-store-outline"
          title="Thông tin cửa hàng"
          value="store"
          rounded="lg"
          to="/store"
          class="mb-1"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- Main Content Area -->
    <v-main>
      <!-- App Bar -->
      <v-app-bar
        :elevation="0"
        :color="darkMode ? 'background' : 'white'"
        height="64"
      >
        <v-container class="d-flex align-center px-4" fluid>
          <!-- Mobile Menu Toggle -->
          <v-btn
            icon
            variant="text"
            @click="drawer = !drawer"
            v-if="isMobile"
          >
            <v-icon>mdi-menu</v-icon>
          </v-btn>

          <!-- Page Title -->
          <h1 class="text-h6 font-weight-medium">{{ pageTitle }}</h1>

          <v-spacer></v-spacer>

          <!-- Search -->
          <v-text-field
            v-if="!isMobile"
            density="compact"
            variant="solo"
            label="Search (Ctrl+/)"
            prepend-inner-icon="mdi-magnify"
            single-line
            hide-details
            flat
            bg-color="background"
            class="rounded-pill search-field mx-4"
            style="max-width: 250px;"
          ></v-text-field>

          <!-- Dark Mode Toggle -->
          <v-btn icon variant="text" @click="toggleDarkMode" class="mr-2">
            <v-icon>{{ darkMode ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
          </v-btn>

          <!-- Notifications -->
          <v-btn icon variant="text" class="mr-2">
            <v-icon>mdi-bell-outline</v-icon>
            <v-badge dot color="error" floating></v-badge>
          </v-btn>

          <!-- User Dropdown Menu -->
          <v-menu
            location="bottom end"
            offset="10"
            transition="slide-y-transition"
          >
            <template v-slot:activator="{ props }">
              <v-avatar
                color="primary"
                size="40"
                v-bind="props"
                class="cursor-pointer"
              >
                <span class="text-subtitle-1 font-weight-medium text-white">US</span>
              </v-avatar>
            </template>
            <v-card min-width="230">
              <v-list density="compact">
                <v-list-item
                  prepend-avatar="https://randomuser.me/api/portraits/men/85.jpg"
                  :title="userInfo.username || 'Admin'"
                  subtitle="Admin"
                >
                  <template v-slot:append>
                    <v-btn
                      icon
                      variant="text"
                      color="medium-emphasis"
                      size="small"
                    >
                      <v-icon>mdi-dots-vertical</v-icon>
                    </v-btn>
                  </template>
                </v-list-item>
              </v-list>
              <v-divider></v-divider>
              <v-list density="compact" nav class="py-0">
                <v-list-item
                  value="profile"
                  prepend-icon="mdi-account-outline"
                  title="Hồ sơ cá nhân"
                  to="/profile"
                ></v-list-item>
                <v-list-item
                  value="logout"
                  prepend-icon="mdi-logout"
                  title="Đăng xuất"
                  @click="logout"
                ></v-list-item>
              </v-list>
            </v-card>
          </v-menu>
        </v-container>
      </v-app-bar>

      <!-- Main Content -->
      <v-container fluid class="pa-6">
        <slot></slot>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useTheme, useDisplay } from 'vuetify'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { authService } from '@/services/authService'

// Setup
const theme = useTheme()
const route = useRoute()
const display = useDisplay()
const appStore = useAppStore()
const router = useRouter()

// Reactive state
const drawer = ref(true)
const rail = ref(false)
const darkMode = ref(false)
const isMobile = ref(false)
const menuItems = ref([])
const userInfo = ref(authService.getUserInfo() || {})

// Computed
const pageTitle = computed(() => route.meta.title || 'Dashboard')

// Initialize state from store
onMounted(() => {
  darkMode.value = appStore.darkMode
  rail.value = appStore.sidebarMini
  drawer.value = appStore.sidebarVisible
  isMobile.value = display.smAndDown.value

  // Adjust sidebar on mobile
  if (isMobile.value) {
    drawer.value = false
  }
})

// Watch for mobile changes
watch(isMobile, (newVal) => {
  if (newVal) {
    drawer.value = false
  } else {
    drawer.value = true
  }
})

// Theme management
watch(darkMode, (newVal) => {
  theme.global.name.value = newVal ? 'dark' : 'light'
  appStore.setDarkMode(newVal)
})

// Methods
function toggleRail() {
  rail.value = !rail.value
  appStore.setSidebarMini(rail.value)
}

function toggleDarkMode() {
  darkMode.value = !darkMode.value
}

async function logout() {
  try {
    // Gọi API đăng xuất
    await authService.logout()
    
    // Xóa dữ liệu người dùng và token
    authService.clearAuthData()
    
    // Chuyển hướng đến trang đăng nhập
    router.push('/login')
  } catch (error) {
    console.error('Lỗi khi đăng xuất:', error)
    
    // Ngay cả khi API lỗi, vẫn xóa dữ liệu và chuyển hướng
    authService.clearAuthData()
    router.push('/login')
  }
}
</script>

<style scoped>
.dashboard-sidebar {
  border-right: 0;
}

.search-field :deep(.v-field__input) {
  min-height: 38px !important;
  padding-top: 9px !important;
}

.cursor-pointer {
  cursor: pointer;
}

.gradient-background {
  background: linear-gradient(to right, #7367F0, #9E69FD) !important;
  color: white !important;
}

/* Hiệu ứng hover cho menu */
:deep(.v-list-item:hover:not(.v-list-item--active)) {
  background-color: rgba(115, 103, 240, 0.08) !important;
}
</style>
