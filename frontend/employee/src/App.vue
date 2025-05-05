<template>
  <v-app>
    <Header 
      v-if="!isLoginRoute"
      :employeeId="authStore.employeeId" 
      :employeeName="authStore.employeeName"
      :accountId="authStore.accountId"
      @updateTheme="handleThemeUpdate"
      @searchProducts="handleSearch"
    />
    
    <v-main :class="{ 'login-main': isLoginRoute }">
      <router-view 
        :employeeId="authStore.employeeId"
        :employeeName="authStore.employeeName"
        :accountId="authStore.accountId"
        :searchQuery="searchQuery"
      />
    </v-main>

    <!-- Global Snackbar -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="snackbar.timeout"
      location="bottom center"
      rounded="pill"
      class="my-snackbar"
    >
      <div class="d-flex align-center">
        <v-icon :icon="snackbar.icon" class="me-2" />
        {{ snackbar.message }}
      </div>
      <template v-slot:actions>
        <v-btn
          variant="text"
          icon="mdi-close"
          @click="snackbar.show = false"
        ></v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTheme } from 'vuetify';
import Header from './components/Header.vue';
import { useSnackbar } from './helpers/useSnackbar';
import { useAuthStore } from './stores/authStore';

// Route
const route = useRoute();
const router = useRouter();

// Auth Store
const authStore = useAuthStore();

// Theme
const theme = useTheme();

// State cho tìm kiếm
const searchQuery = ref('');

// Hàm xử lý tìm kiếm
const handleSearch = (query) => {
  console.log("App received search query:", query);
  searchQuery.value = query;
};

// Hàm xử lý cập nhật theme
const handleThemeUpdate = (newTheme) => {
  theme.global.name.value = newTheme;
};

// Kiểm tra có phải là trang đăng nhập không
const isLoginRoute = computed(() => {
  return route.path === '/login';
});

// Snackbar
const { snackbar } = useSnackbar();

// Watch for route changes to redirect if not authenticated
watch(() => route.path, (newPath) => {
  if (newPath !== '/login' && !authStore.isLoggedIn) {
    console.log('App Watcher: Not logged in, redirecting to /login');
    router.push('/login');
  }
});

// Watch for changes in the store's logged-in state
watch(() => authStore.isLoggedIn, (isLoggedIn) => {
  if (!isLoggedIn && !isLoginRoute.value) {
    console.log('App Watcher: authStore.isLoggedIn changed to false, redirecting to /login');
    router.push('/login');
  }
});

// Khởi tạo khi component được mount
onMounted(() => {
  authStore.initializeAuth();
  
  // Khôi phục theme từ localStorage nếu có
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme) {
    theme.global.name.value = savedTheme;
  }
});
</script>

<style>
.main-container {
  height: calc(100vh - 64px);
  overflow: hidden;
}

.login-main {
  height: 100vh;
}

.main-content {
  height: 100%;
}

.left-panel {
  flex: 7;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid rgba(var(--v-border-opacity, 1), 0.12);
}

.right-panel {
  flex: 3;
  display: flex;
  flex-direction: column;
  background-color: var(--v-theme-surface);
  min-width: 360px;
}

.my-snackbar {
  font-weight: 500;
}
</style>