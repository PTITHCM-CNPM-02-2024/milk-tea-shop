import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // State
  const darkMode = ref(false)
  const sidebarMini = ref(false)
  const sidebarVisible = ref(true)
  
  // Actions
  function setDarkMode(value) {
    darkMode.value = value
    // Lưu vào localStorage để duy trì trạng thái sau khi refresh
    localStorage.setItem('darkMode', value)
  }
  
  function toggleDarkMode() {
    darkMode.value = !darkMode.value
    // Lưu vào localStorage để duy trì trạng thái sau khi refresh
    localStorage.setItem('darkMode', darkMode.value)
  }
  
  function setSidebarMini(value) {
    sidebarMini.value = value
  }
  
  function toggleSidebarMini() {
    sidebarMini.value = !sidebarMini.value
  }
  
  function setSidebarVisible(value) {
    sidebarVisible.value = value
  }
  
  function toggleSidebarVisible() {
    sidebarVisible.value = !sidebarVisible.value
  }
  
  // Khởi tạo giá trị từ localStorage nếu có
  function initStore() {
    const savedDarkMode = localStorage.getItem('darkMode')
    if (savedDarkMode !== null) {
      darkMode.value = savedDarkMode === 'true'
    }
  }
  
  // Gọi hàm khởi tạo
  initStore()
  
  return {
    darkMode,
    sidebarMini,
    sidebarVisible,
    setDarkMode,
    toggleDarkMode,
    setSidebarMini,
    toggleSidebarMini,
    setSidebarVisible,
    toggleSidebarVisible
  }
}) 