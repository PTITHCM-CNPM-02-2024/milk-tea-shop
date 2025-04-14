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

      <v-spacer></v-spacer>

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
              <div class="text-caption">Nhân viên bán hàng</div>
            </div>
            <v-avatar color="secondary" size="36">
              <v-icon color="white">mdi-account</v-icon>
            </v-avatar>
          </div>
        </template>
        
        <v-list>
          <v-list-item prepend-icon="mdi-account-outline" title="Hồ sơ" to="/profile"></v-list-item>
          <v-divider></v-divider>
          <v-list-item prepend-icon="mdi-logout" title="Đăng xuất" color="error"></v-list-item>
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
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
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

const route = useRoute();
const router = useRouter();
const drawer = ref(false);
const currentDateTime = ref('');
let timer = null;

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
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.sidebar-drawer {
  z-index: 1001;
}

/* Tạo hiệu ứng active cho các menu item */
:deep(.v-list-item--active) {
  background-color: rgba(var(--v-theme-primary), 0.1);
}

:deep(.v-list-item--active .v-list-item-title) {
  color: rgb(var(--v-theme-primary));
  font-weight: bold;
}
</style>