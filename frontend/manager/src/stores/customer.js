import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { customerService } from '@/services/customerService'

export const useCustomerStore = defineStore('customer', () => {
  // State
  const customers = ref([])
  const totalCustomers = ref(0)
  const membershipTypes = ref([])
  const totalMembershipTypes = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const currentCustomerPage = ref(0)
  const currentMembershipPage = ref(0)
  const pageSize = ref(10)

  // Getters
  const totalCustomerPages = computed(() => Math.ceil(totalCustomers.value / pageSize.value))
  const totalMembershipPages = computed(() => Math.ceil(totalMembershipTypes.value / pageSize.value))

  // Lấy danh sách khách hàng
  async function fetchCustomers(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentCustomerPage.value = page

      const response = await customerService.getCustomers(page, size)
      customers.value = response.data.content || []
      totalCustomers.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải danh sách khách hàng'
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
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải thông tin khách hàng'
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
      await fetchCustomers(currentCustomerPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tạo khách hàng mới'
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
      await fetchCustomers(currentCustomerPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật khách hàng'
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
      await fetchCustomers(currentCustomerPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi xóa khách hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Lấy danh sách loại thành viên
  async function fetchMembershipTypes(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentMembershipPage.value = page

      const response = await customerService.getMembershipTypes(page, size)
      // API trả về trực tiếp là danh sách không có phân trang
      membershipTypes.value = response.data || []
      totalMembershipTypes.value = response.data.length || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách loại thành viên'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Tạo loại thành viên mới
  async function createMembershipType(data) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.createMembershipType(data)
      // Cập nhật danh sách loại thành viên
      await fetchMembershipTypes(currentMembershipPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tạo loại thành viên mới'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Cập nhật loại thành viên
  async function updateMembershipType(id, data) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.updateMembershipType(id, data)
      // Cập nhật danh sách loại thành viên
      await fetchMembershipTypes(currentMembershipPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật loại thành viên'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Xóa loại thành viên
  async function deleteMembershipType(id) {
    loading.value = true
    error.value = null

    try {
      const response = await customerService.deleteMembershipType(id)
      // Cập nhật danh sách loại thành viên
      await fetchMembershipTypes(currentMembershipPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi xóa loại thành viên'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    customers,
    totalCustomers,
    membershipTypes,
    totalMembershipTypes,
    loading,
    error,
    currentCustomerPage,
    currentMembershipPage,
    pageSize,

    // Getters
    totalCustomerPages,
    totalMembershipPages,

    // Actions
    fetchCustomers,
    fetchCustomerById,
    createCustomer,
    updateCustomer,
    deleteCustomer,
    fetchMembershipTypes,
    createMembershipType,
    updateMembershipType,
    deleteMembershipType
  }
})
