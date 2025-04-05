<template>
  <v-app>
    <Header 
      :employeeId="employeeId" 
      :employeeName="employeeName"
    />
    
    <v-main>
      <router-view 
        :employeeId="employeeId"
        :employeeName="employeeName"
      />
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue';
import Header from './components/Header.vue';

// Nhân viên hiện tại (trong thực tế sẽ lấy từ đăng nhập)
const employeeId = ref(4139561627);
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