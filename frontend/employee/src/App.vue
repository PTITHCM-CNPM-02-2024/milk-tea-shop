<template>
  <v-app>
    <Header 
      v-if="!isLoginRoute"
      :employeeId="employeeId" 
      :employeeName="employeeName"
      :accountId="accountId"
    />
    
    <v-main :class="{ 'login-main': isLoginRoute }">
      <router-view 
        :employeeId="employeeId"
        :employeeName="employeeName"
        :accountId="accountId"
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
import Header from './components/Header.vue';
import { useSnackbar } from './helpers/useSnackbar';
import AuthService from './services/auth.service';

// Route
const route = useRoute();
const router = useRouter();

// Kiểm tra có phải là trang đăng nhập không
const isLoginRoute = computed(() => {
  return route.path === '/login';
});

// Snackbar
const { snackbar } = useSnackbar();

// Thông tin nhân viên
const accountId = ref(null);
const employeeId = ref(null);
const employeeName = ref('');

// Hàm lấy thông tin nhân viên từ localStorage
const fetchEmployeeInfo = async () => {
  try {
    const storedAccountId = AuthService.getAccountId();
    const storedEmployeeId = AuthService.getEmployeeId();
    
    if (storedAccountId && storedEmployeeId) {
      accountId.value = parseInt(storedAccountId);
      employeeId.value = parseInt(storedEmployeeId);
      
      // Lấy thông tin chi tiết nhân viên từ API (nếu cần)
      try {
        const response = await AuthService.getEmployeeByAccountId(storedAccountId);
        if (response && response.data) {
          employeeName.value = response.data.name || 'Nhân viên';
        }
      } catch (error) {
        console.error('Lỗi khi lấy thông tin nhân viên:', error);
        employeeName.value = 'Nhân viên';
      }
    } else if (!isLoginRoute.value) {
      // Nếu không có thông tin nhân viên và không ở trang login, chuyển về trang login
      router.push('/login');
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin người dùng:', error);
    if (!isLoginRoute.value) {
      router.push('/login');
    }
  }
};

// Theo dõi thay đổi của route
watch(() => route.path, (newPath) => {
  if (newPath !== '/login' && !AuthService.isAuthenticated()) {
    router.push('/login');
  }
});

// Khởi tạo thông tin nhân viên khi component được mount
onMounted(() => {
  // Thiết lập token cho API requests nếu đã đăng nhập
  const token = AuthService.getAuthToken();
  if (token) {
    AuthService.setAuthHeader(token);
    fetchEmployeeInfo();
  } else if (!isLoginRoute.value) {
    router.push('/login');
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
  border-right: 1px solid rgba(0, 0, 0, 0.12);
}

.right-panel {
  flex: 3;
  display: flex;
  flex-direction: column;
  background-color: white;
  min-width: 360px;
}

.my-snackbar {
  font-weight: 500;
}
</style>