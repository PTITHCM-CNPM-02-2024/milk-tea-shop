<script setup>
// import Login from './components/MainLayout/Login.vue'
// import { HomeIcon, Cog6ToothIcon, ShoppingCartIcon, HeartIcon } from '@heroicons/vue/24/outline'
// import Posdasboard from './components/MainLayout/SideBar.vue'
//
import SideBar from './components/MainLayout/SideBar.vue'
</script>

<template>
  <div class="flex">
    <!-- component dang nhap  -->
    <!-- <Login /> -->
    <!-- menu dieu huong -->
    <SideBar />
    <div class="flex-1 p-4">
      <router-view />
    </div>
    <!-- <ProductList /> -->
    <!-- danh sach nhan vien -->
    <!-- <EmployeeList /> -->
    <!-- <EmployeeManagementNew /> -->
  </div>
</template>
