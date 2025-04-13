import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { accountService } from '@/services/accountService'

export const useAccountStore = defineStore('account', () => {
  // State
  const accounts = ref([])
  const totalAccounts = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const roles = ref([
    { id: 1, name: 'MANAGER', description: 'Quản lý' },
    { id: 2, name: 'STAFF', description: 'Nhân viên' },
    { id: 3, name: 'CUSTOMER', description: 'Khách hàng' },
    { id: 4, name: 'GUEST', description: 'Khách' }
  ])

  // Getter
  const totalPages = computed(() => Math.ceil(totalAccounts.value / pageSize.value))

  // Lấy danh sách tài khoản
  async function fetchAccounts(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentPage.value = page
      pageSize.value = size

      const response = await accountService.getAccounts(page, size)
      accounts.value = response.data.content || []
      totalAccounts.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Lấy thông tin chi tiết tài khoản
  async function fetchAccountById(id) {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.getAccountById(id)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải thông tin tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Tạo tài khoản mới
  async function createAccount(accountData) {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.createAccount(accountData)
      // Cập nhật danh sách tài khoản
      await fetchAccounts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tạo tài khoản mới'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Cập nhật tài khoản
  async function updateAccount(id, accountData) {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.updateAccount(id, accountData)
      // Cập nhật danh sách tài khoản
      await fetchAccounts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi cập nhật tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Xóa tài khoản
  async function deleteAccount(id) {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.deleteAccount(id)
      // Cập nhật danh sách tài khoản
      await fetchAccounts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi xóa tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Khóa/Mở khóa tài khoản
  async function toggleAccountLock(id, isLocked) {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.toggleAccountLock(id, isLocked)
      // Cập nhật danh sách tài khoản
      await fetchAccounts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi thay đổi trạng thái khóa tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Kích hoạt/Vô hiệu hóa tài khoản
  async function toggleAccountActive(id, isActive) {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.toggleAccountActive(id, isActive)
      // Cập nhật danh sách tài khoản
      await fetchAccounts(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi thay đổi trạng thái kích hoạt tài khoản'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Lấy danh sách vai trò
  async function fetchRoles() {
    loading.value = true
    error.value = null

    try {
      const response = await accountService.getRoles()
      roles.value = response.data || roles.value
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải danh sách vai trò'
      // Không throw lỗi, sử dụng danh sách vai trò mặc định
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    accounts,
    totalAccounts,
    loading,
    error,
    currentPage,
    pageSize,
    roles,

    // Getters
    totalPages,

    // Actions
    fetchAccounts,
    fetchAccountById,
    createAccount,
    updateAccount,
    deleteAccount,
    toggleAccountLock,
    toggleAccountActive,
    fetchRoles
  }
})
