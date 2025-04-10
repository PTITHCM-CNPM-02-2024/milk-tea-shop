import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { employeeService } from '@/services/employeeService'

export const useEmployeeStore = defineStore('employee', () => {
  // State
  const employees = ref([])
  const totalEmployees = ref(0) 
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)
  
  // Getter
  const totalPages = computed(() => Math.ceil(totalEmployees.value / pageSize.value))
  
  // Lấy danh sách nhân viên
  async function fetchEmployees(page = 0, size = 10) {
    loading.value = true
    error.value = null
    
    try {
      currentPage.value = page
      pageSize.value = size
      
      const response = await employeeService.getEmployees(page, size)
      employees.value = response.data.content || []
      totalEmployees.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách nhân viên'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Lấy thông tin chi tiết nhân viên
  async function fetchEmployeeById(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await employeeService.getEmployeeById(id)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải thông tin nhân viên'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Tạo nhân viên mới
  async function createEmployee(employeeData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await employeeService.createEmployee(employeeData)
      // Cập nhật danh sách nhân viên
      await fetchEmployees(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tạo nhân viên mới'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Cập nhật nhân viên
  async function updateEmployee(id, employeeData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await employeeService.updateEmployee(id, employeeData)
      // Cập nhật danh sách nhân viên
      await fetchEmployees(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật nhân viên'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Xóa nhân viên
  async function deleteEmployee(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await employeeService.deleteEmployee(id)
      // Cập nhật danh sách nhân viên
      await fetchEmployees(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi xóa nhân viên'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    // State
    employees,
    totalEmployees,
    loading,
    error,
    currentPage,
    pageSize,
    
    // Getters
    totalPages,
    
    // Actions
    fetchEmployees,
    fetchEmployeeById,
    createEmployee,
    updateEmployee,
    deleteEmployee
  }
}) 