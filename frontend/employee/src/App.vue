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
    >
      {{ snackbar.message }}
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
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import Header from './components/Header.vue';
import { useSnackbar } from './helpers/useSnackbar';

// Route
const route = useRoute();

// Kiểm tra có phải là trang đăng nhập không
const isLoginRoute = computed(() => {
  return route.path === '/login';
});

// Snackbar
const { snackbar } = useSnackbar();

// Nhân viên hiện tại (trong thực tế sẽ lấy từ đăng nhập)
const accountId = ref(2);
const employeeId = ref(1);
const employeeName = ref('Phạm Văn A');
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
</style>