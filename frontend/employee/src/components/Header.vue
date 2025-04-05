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

      <!-- Menu người dùng -->
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
          <v-list-item prepend-icon="mdi-account-outline" title="Hồ sơ"></v-list-item>
          <v-list-item prepend-icon="mdi-clock-time-four-outline" title="Ca làm việc"></v-list-item>
          <v-divider></v-divider>
          <v-list-item prepend-icon="mdi-logout" title="Đăng xuất" color="error"></v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const props = defineProps({
  employeeId: {
    type: Number,
    default: 0
  },
  employeeName: {
    type: String,
    default: 'Nhân viên'
  }
});

const route = useRoute();
const router = useRouter();
const drawer = ref(false);
const currentDateTime = ref('');
let timer = null;

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