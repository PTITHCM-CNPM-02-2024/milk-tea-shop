import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { roleService } from '@/services/roleService'

export const useRoleStore = defineStore('role', () => {
  // State
  const roles = ref([])
  const totalRoles = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)
  
  // Getters
  const totalPages = computed(() => Math.ceil(totalRoles.value / pageSize.value))
  
  // Lấy danh sách vai trò
  async function fetchRoles(page = 0, size = 10) {
    loading.value = true
    error.value = null
    
    try {
      currentPage.value = page
      pageSize.value = size
      
      const response = await roleService.getAllRoles(page, size)
      roles.value = response.data.content || []
      console.log(roles.value)
      totalRoles.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách vai trò'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Lấy thông tin chi tiết vai trò
  async function fetchRoleById(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await roleService.getRoleById(id)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải thông tin vai trò'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Tạo vai trò mới
  async function createRole(roleData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await roleService.createRole(roleData)
      // Cập nhật danh sách vai trò
      await fetchRoles(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tạo vai trò mới'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật vai trò
  async function updateRole(id, roleData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await roleService.updateRole(id, roleData)
      // Cập nhật danh sách vai trò
      await fetchRoles(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật vai trò'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Xóa vai trò
  async function deleteRole(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await roleService.deleteRole(id)
      // Cập nhật danh sách vai trò
      await fetchRoles(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi xóa vai trò'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    // State
    roles,
    totalRoles,
    loading,
    error,
    currentPage,
    pageSize,
    
    // Getters
    totalPages,
    
    // Actions
    fetchRoles,
    fetchRoleById,
    createRole,
    updateRole,
    deleteRole
  }
}) 