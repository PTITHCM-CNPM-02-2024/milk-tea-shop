import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { customerService } from '@/services/customerService'
import { accountService } from '@/services/accountService'

export const useCustomerStore = defineStore('customer', () => {
  // State
  const customers = ref([])
  const totalCustomers = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)

  // Getters
  const totalPages = computed(() => Math.ceil(totalCustomers.value / pageSize.value))

  // Lấy danh sách khách hàng
  async function fetchCustomers(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentPage.value = page

      const response = await customerService.getCustomers(page, size)
      customers.value = response.data.content || []
      totalCustomers.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchCustomers:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách khách hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Lấy thông tin chi tiết khách hàng
  async function fetchCustomerById(id) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.getCustomerById(id)
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchCustomerById:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải thông tin khách hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Tạo khách hàng mới
  async function createCustomer(customerData) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.createCustomer(customerData)
      // Cập nhật danh sách khách hàng
      await fetchCustomers(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong createCustomer:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tạo khách hàng mới'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Cập nhật khách hàng
  async function updateCustomer(id, customerData) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.updateCustomer(id, customerData)
      // Cập nhật danh sách khách hàng
      await fetchCustomers(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong updateCustomer:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật khách hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Xóa khách hàng
  async function deleteCustomer(id) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.deleteCustomer(id)
      // Cập nhật danh sách khách hàng
      await fetchCustomers(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong deleteCustomer:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi xóa khách hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Khóa/Mở khóa tài khoản khách hàng
  async function toggleAccountLock(accountId, isLocked) {
    loading.value = true
    error.value = null
    
    try {
      const response = await accountService.toggleAccountLock(accountId, isLocked)
      return response.data
    } catch (err) {
      console.error('Lỗi trong toggleAccountLock (customerStore):', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi thay đổi trạng thái khóa tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    customers,
    totalCustomers,
    loading,
    error,
    currentPage,
    pageSize,

    // Getters
    totalPages,

    // Actions
    fetchCustomers,
    fetchCustomerById,
    createCustomer,
    updateCustomer,
    deleteCustomer,
    toggleAccountLock
  }
})
