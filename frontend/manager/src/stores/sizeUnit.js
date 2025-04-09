import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { productService } from '@/services/productService'

export const useSizeUnitStore = defineStore('sizeUnit', () => {
  // State
  const sizeUnits = ref([])
  const totalSizeUnits = ref(0) 
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)
  
  // Getter
  const totalPages = computed(() => Math.ceil(totalSizeUnits.value / pageSize.value))
  
  // Lấy danh sách kích thước và đơn vị tính
  async function fetchSizeUnits(page = 0, size = 10) {
    loading.value = true
    error.value = null
    
    try {
      currentPage.value = page
      pageSize.value = size
      
      const response = await productService.getSizeUnits(page, size)
      sizeUnits.value = response.data.content || []
      totalSizeUnits.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách kích thước và đơn vị tính'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Tạo kích thước/đơn vị tính mới
  async function createSizeUnit(sizeUnitData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.createSizeUnit(sizeUnitData)
      // Cập nhật danh sách kích thước/đơn vị tính
      await fetchSizeUnits(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tạo kích thước/đơn vị tính mới'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật kích thước/đơn vị tính
  async function updateSizeUnit(id, sizeUnitData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.updateSizeUnit(id, sizeUnitData)
      // Cập nhật danh sách kích thước/đơn vị tính
      await fetchSizeUnits(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật kích thước/đơn vị tính'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Xóa kích thước/đơn vị tính
  async function deleteSizeUnit(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await productService.deleteSizeUnit(id)
      // Cập nhật danh sách kích thước/đơn vị tính
      await fetchSizeUnits(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi xóa kích thước/đơn vị tính'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    // State
    sizeUnits,
    totalSizeUnits,
    loading,
    error,
    currentPage,
    pageSize,
    
    // Getters
    totalPages,
    
    // Actions
    fetchSizeUnits,
    createSizeUnit,
    updateSizeUnit,
    deleteSizeUnit
  }
}) 