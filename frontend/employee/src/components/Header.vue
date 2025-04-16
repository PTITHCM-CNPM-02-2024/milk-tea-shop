<template>
  <div>
    <!-- Navigation Drawer -->
    <v-navigation-drawer
      v-model="drawer"
      temporary
      :width="240"
      location="left"
      class="sidebar-drawer"
    >
      <v-list-item
        title="POS Trà Sữa"
        subtitle="Quản lý bán hàng"
      >
        <template v-slot:prepend>
          <v-avatar color="primary">
            <v-icon color="white">mdi-coffee</v-icon>
          </v-avatar>
        </template>
      </v-list-item>

      <v-divider class="my-2"></v-divider>

      <v-list nav>
        <v-list-item 
          prepend-icon="mdi-cash-register" 
          title="Bán hàng" 
          @click="navigateTo('/')"
          :active="currentRoute === '/'"
          value="pos"
        ></v-list-item>
        <v-list-item 
          prepend-icon="mdi-cart-outline" 
          title="Đơn hàng" 
          @click="navigateTo('/orders')"
          :active="currentRoute === '/orders'"
          value="orders"
        ></v-list-item>
        <v-list-item 
          prepend-icon="mdi-book-open-variant" 
          title="Báo cáo" 
          @click="navigateTo('/reports')"
          :active="currentRoute === '/reports'"
          value="reports"
        ></v-list-item>
      </v-list>
      
      <template v-slot:append>
        <v-divider></v-divider>
        <div class="pa-3">
          <v-btn block color="primary" prepend-icon="mdi-help-circle">
            Trợ giúp
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- App Bar -->
    <v-app-bar color="primary" elevation="2" class="app-header">
      <v-app-bar-nav-icon @click="drawer = !drawer" color="white"></v-app-bar-nav-icon>
      
      <div class="d-flex align-center">
        <v-avatar color="white" class="me-3" size="36">
          <v-icon color="primary">mdi-coffee</v-icon>
        </v-avatar>
        <v-app-bar-title>POS Trà Sữa</v-app-bar-title>
      </div>

      <!-- Thanh tìm kiếm sản phẩm -->
      <div class="search-container ml-4 d-none d-md-flex">
        <v-text-field
          v-model="searchQuery"
          density="compact"
          variant="solo"
          hide-details
          placeholder="Tìm kiếm sản phẩm..."
          prepend-inner-icon="mdi-magnify"
          class="search-input"
          single-line
          rounded
          clearable
          @input="handleSearch"
          @click:clear="clearSearch"
        ></v-text-field>
      </div>

      <v-spacer></v-spacer>

      <!-- Nút tìm kiếm cho màn hình nhỏ -->
      <v-btn icon class="d-flex d-md-none me-2" variant="text" @click="showSearchBar = !showSearchBar">
        <v-icon>{{ showSearchBar ? 'mdi-close' : 'mdi-magnify' }}</v-icon>
      </v-btn>

      <!-- Nút chuyển đổi theme -->
      <v-btn icon class="me-2" variant="text" @click="toggleTheme" :title="isDarkTheme ? 'Chuyển sang chế độ sáng' : 'Chuyển sang chế độ tối'">
        <v-icon>{{ isDarkTheme ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
      </v-btn>

      <!-- Nút thông báo -->
      <v-btn icon class="me-2" variant="text">
        <v-badge color="error" content="3" dot>
          <v-icon>mdi-bell-outline</v-icon>
        </v-badge>
      </v-btn>

      <div class="d-none d-sm-flex align-center me-3">
        <v-icon class="me-2">mdi-clock-outline</v-icon>
        <span class="text-caption">{{ currentDateTime }}</span>
      </div>

      <v-divider vertical class="mx-2 d-none d-sm-flex"></v-divider>

      <!-- User Menu -->
      <v-menu location="bottom end" transition="slide-y-transition">
        <template v-slot:activator="{ props }">
          <div class="d-flex align-center" v-bind="props" style="cursor: pointer">
            <div class="me-2 text-right d-none d-sm-block">
              <div class="text-body-2 font-weight-medium">{{ employeeName }}</div>
              <div class="text-caption">ID: {{ employeeId }} - Nhân viên bán hàng</div>
            </div>
            <v-avatar color="secondary" size="36">
              <v-icon color="white">mdi-account</v-icon>
            </v-avatar>
          </div>
        </template>
        
        <v-list>
          <v-list-item prepend-icon="mdi-account-outline" title="Hồ sơ" to="/profile"></v-list-item>
          <v-divider></v-divider>
          <v-list-item prepend-icon="mdi-logout" title="Đăng xuất" color="error" @click="showLogoutDialog = true"></v-list-item>
        </v-list>
      </v-menu>

      <!-- Thêm nút đăng xuất ở cuối header -->
      <v-btn
        icon
        variant="text"
        @click="showLogoutDialog = true"
        title="Đăng xuất"
      >
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <!-- Thanh tìm kiếm cho màn hình nhỏ -->
    <div v-if="showSearchBar" class="mobile-search-bar">
      <v-text-field
        v-model="searchQuery"
        density="compact"
        variant="solo"
        hide-details
        placeholder="Tìm kiếm sản phẩm..."
        prepend-inner-icon="mdi-magnify"
        append-icon="mdi-close"
        class="search-input"
        single-line
        rounded
        autofocus
        @input="handleSearch"
        @click:append="showSearchBar = false"
      ></v-text-field>
    </div>

    <!-- Dialog xác nhận đăng xuất -->
    <v-dialog v-model="showLogoutDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">Xác nhận đăng xuất</v-card-title>
        <v-card-text>
          Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="showLogoutDialog = false">
            Hủy
          </v-btn>
          <v-btn color="red-darken-1" variant="text" @click="logout" :loading="isLoggingOut">
            Đăng xuất
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, inject, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTheme } from 'vuetify';
import AuthService from '../services/auth.service';

const props = defineProps({
  employeeId: {
    type: Number,
    default: 0
  },
  employeeName: {
    type: String,
    default: 'Nhân viên'
  },
  accountId: {
    type: Number,
    default: 2
  }
});

// Emit event để thông báo thay đổi theme và tìm kiếm cho component cha
const emit = defineEmits(['updateTheme', 'searchProducts']);

const route = useRoute();
const router = useRouter();
const drawer = ref(false);
const currentDateTime = ref('');
let timer = null;

// State cho thanh tìm kiếm
const searchQuery = ref('');
const showSearchBar = ref(false);

// Xử lý tìm kiếm
function handleSearch() {
  emit('searchProducts', searchQuery.value);
}

// Xử lý xóa tìm kiếm
function clearSearch() {
  searchQuery.value = '';
  emit('searchProducts', '');
}

// Thêm watcher cho searchQuery để xử lý khi giá trị thay đổi
watch(searchQuery, (newValue) => {
  emit('searchProducts', newValue);
});

// Theme
const theme = useTheme();
const isDarkTheme = computed(() => theme.global.current.value.dark);

// Hàm chuyển đổi theme
function toggleTheme() {
  theme.global.name.value = isDarkTheme.value ? 'lightTheme' : 'darkTheme';
  
  // Lưu theme vào localStorage để duy trì sau khi refresh
  localStorage.setItem('theme', theme.global.name.value);
  
  // Emit event lên component cha
  emit('updateTheme', theme.global.name.value);
}

// State cho đăng xuất
const showLogoutDialog = ref(false);
const isLoggingOut = ref(false);

// Tính toán đường dẫn hiện tại để active menu
const currentRoute = computed(() => {
  return route.path;
});

// Hàm điều hướng
function navigateTo(path) {
  // Đóng drawer sau khi chọn menu
  drawer.value = false;
  
  // Điều hướng
  if (route.path !== path) {
    router.push(path);
  }
}

function updateDateTime() {
  const now = new Date();
  const options = {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  };
  currentDateTime.value = now.toLocaleDateString('vi-VN', options);
}

onMounted(() => {
  updateDateTime();
  timer = setInterval(updateDateTime, 60000);
  
  // Khôi phục theme từ localStorage nếu đã lưu
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme) {
    theme.global.name.value = savedTheme;
  }
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});

// Hàm đăng xuất
async function logout() {
  isLoggingOut.value = true;
  
  try {
    await AuthService.logout();
    router.push('/login');
  } catch (error) {
    console.error('Lỗi khi đăng xuất:', error);
    // Xóa thông tin đăng nhập để đảm bảo người dùng vẫn được đăng xuất
    AuthService.clearAuth();
    router.push('/login');
  } finally {
    showLogoutDialog.value = false;
    isLoggingOut.value = false;
  }
}
</script>

<style scoped>
.app-header {
  border-bottom: 1px solid rgba(var(--v-border-opacity, 1), 0.12);
}

.sidebar-drawer {
  z-index: 1001;
}

.search-container {
  width: 300px;
  max-width: 100%;
}

.search-input {
  background-color: rgba(255, 255, 255, 0.2) !important;
  border-radius: 25px;
}

.search-input :deep(.v-field__field) {
  color: white !important;
}

.search-input :deep(.v-field__input) {
  min-height: 36px !important;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
}

.search-input :deep(.v-field__input input::placeholder) {
  color: rgba(255, 255, 255, 0.8);
}

.search-input :deep(.v-field__prepend-inner) {
  opacity: 0.8;
  color: white;
}

.search-input :deep(.v-field__append-inner) {
  opacity: 0.8;
  color: white;
}

.mobile-search-bar {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  padding: 8px 16px;
  background-color: var(--v-theme-primary);
  z-index: 1000;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
  }
  to {
    transform: translateY(0);
  }
}

/* Tạo hiệu ứng active cho các menu item */
:deep(.v-list-item--active) {
  background-color: rgba(var(--v-theme-primary-rgb, 25, 118, 210), 0.1);
}

:deep(.v-list-item--active .v-list-item-title) {
  color: rgb(var(--v-theme-primary-rgb, 25, 118, 210));
  font-weight: bold;
}
</style>