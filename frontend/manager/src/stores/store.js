import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storeService } from '@/services/storeService'

export const useStoreInfoStore = defineStore('storeInfo', () => {
  // State
  const storeInfo = ref({
    id: null,
    name: '',
    address: '',
    phone: '',
    email: '',
    taxCode: '',
    openTime: null,
    closeTime: null,
    openingDate: ''
  })
  const loading = ref(false)
  const error = ref(null)
  
  // Getters
  const formattedOpeningDate = computed(() => {
    if (!storeInfo.value.openingDate) return ''
    return new Date(storeInfo.value.openingDate).toLocaleDateString('vi-VN')
  })
  
  const formattedOpenTime = computed(() => {
    if (!storeInfo.value.openTime) return ''
    return storeInfo.value.openTime
  })
  
  const formattedCloseTime = computed(() => {
    if (!storeInfo.value.closeTime) return ''
    return storeInfo.value.closeTime
  })
  
  // Lấy thông tin cửa hàng
  async function fetchStoreInfo() {
    loading.value = true
    error.value = null
    
    try {
      const response = await storeService.getStoreInfo()
      storeInfo.value = response.data
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải thông tin cửa hàng'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật thông tin cửa hàng
  async function updateStoreInfo(data) {
    loading.value = true
    error.value = null
    
    try {
      const response = await storeService.updateStoreInfo(data)
      storeInfo.value = response.data
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi cập nhật thông tin cửa hàng'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    // State
    storeInfo,
    loading,
    error,
    
    // Getters
    formattedOpeningDate,
    formattedOpenTime,
    formattedCloseTime,
    
    // Actions
    fetchStoreInfo,
    updateStoreInfo
  }
}) 