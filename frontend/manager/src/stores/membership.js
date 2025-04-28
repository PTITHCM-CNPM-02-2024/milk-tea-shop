import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { membershipService } from '@/services/membershipService'

export const useMembershipStore = defineStore('membership', () => {
  // State
  const membershipTypes = ref([])
  const totalMembershipTypes = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const currentPage = ref(0)
  const pageSize = ref(10)

  // Getters
  const totalPages = computed(() => Math.ceil(totalMembershipTypes.value / pageSize.value))

  // Lấy danh sách loại thành viên
  async function fetchMembershipTypes(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentPage.value = page

      const response = await membershipService.getMembershipTypes(page, size)
      // API trả về trực tiếp là danh sách không có phân trang
      membershipTypes.value = response.data || []
      totalMembershipTypes.value = response.data.length || 0
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải danh sách loại thành viên'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Lấy thông tin chi tiết loại thành viên
  async function fetchMembershipTypeById(id) {
    loading.value = true
    error.value = null

    try {
      const response = await membershipService.getMembershipTypeById(id)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tải thông tin loại thành viên'
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
      const response = await membershipService.createMembershipType(data)
      // Cập nhật danh sách loại thành viên
      await fetchMembershipTypes(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi tạo loại thành viên mới'
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
      const response = await membershipService.updateMembershipType(id, data)
      // Cập nhật danh sách loại thành viên
      await fetchMembershipTypes(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi cập nhật loại thành viên'
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
      const response = await membershipService.deleteMembershipType(id)
      // Cập nhật danh sách loại thành viên
      await fetchMembershipTypes(currentPage.value, pageSize.value)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Đã xảy ra lỗi khi xóa loại thành viên'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    membershipTypes,
    totalMembershipTypes,
    loading,
    error,
    currentPage,
    pageSize,

    // Getters
    totalPages,

    // Actions
    fetchMembershipTypes,
    fetchMembershipTypeById,
    createMembershipType,
    updateMembershipType,
    deleteMembershipType
  }
}) 