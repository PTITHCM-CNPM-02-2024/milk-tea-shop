import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8181/api/v1'

export const accountService = {
  // Lấy danh sách tài khoản với phân trang
  getAccounts(page = 0, size = 10) {
    return axios.get(`${API_URL}/accounts`, {
      params: { page, size }
    })
  },

  // Lấy thông tin chi tiết tài khoản
  getAccountById(id) {
    return axios.get(`${API_URL}/accounts/${id}`)
  },

  // Tạo tài khoản mới
  createAccount(accountData) {
    return axios.post(`${API_URL}/accounts`, accountData)
  },

  // Cập nhật tài khoản
  updateAccount(id, accountData) {
    return axios.put(`${API_URL}/accounts/${id}`, accountData)
  },

  // Xóa tài khoản
  deleteAccount(id) {
    return axios.delete(`${API_URL}/accounts/${id}`)
  },

  // Lấy danh sách vai trò
  getRoles() {
    return axios.get(`${API_URL}/roles`)
  },

  // Khóa/Mở khóa tài khoản
  toggleAccountLock(id, isLocked) {
    return axios.patch(`${API_URL}/accounts/${id}/lock`, { isLocked })
  },

  // Kích hoạt/Vô hiệu hóa tài khoản
  toggleAccountActive(id, isActive) {
    return axios.patch(`${API_URL}/accounts/${id}/activate`, { isActive })
  }
}
