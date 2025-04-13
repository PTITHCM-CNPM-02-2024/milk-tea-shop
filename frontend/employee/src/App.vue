<template>
  <v-app>
    <Header 
      :employeeId="employeeId" 
      :employeeName="employeeName"
      :accountId="accountId"
    />
    
    <v-main>
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
import { ref } from 'vue';
import Header from './components/Header.vue';
import { useSnackbar } from './helpers/useSnackbar';

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