import { defineStore } from 'pinia'
import { ref } from 'vue'
import { managerService } from '@/services/managerService'

export const useManagerStore = defineStore('manager', () => {
  // State
  const manager = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // Mặc định ID của manager là 2 (theo yêu cầu)
  const defaultManagerId = 1

  // Lấy thông tin chi tiết manager
  async function fetchManagerProfile() {
    loading.value = true
    error.value = null

    try {
      const response = await managerService.getManagerById(defaultManagerId)
      manager.value = response.data
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải thông tin hồ sơ'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Cập nhật thông tin manager
  async function updateManagerProfile(managerData) {
    loading.value = true
    error.value = null

    try {
      const response = await managerService.updateManager(defaultManagerId, managerData)
      // Cập nhật thông tin manager trong store
      manager.value = {...manager.value, ...managerData}
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi cập nhật thông tin hồ sơ'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    manager,
    loading,
    error,
    defaultManagerId,

    // Actions
    fetchManagerProfile,
    updateManagerProfile
  }
})
