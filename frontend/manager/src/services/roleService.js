import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8181/api/v1'

export const roleService = {
  // Lấy danh sách vai trò với phân trang
  getAllRoles(page = 0, size = 10) {
    return axios.get(`${API_URL}/roles`, {
      params: { page, size }
    })
  },

  // Lấy thông tin chi tiết vai trò
  getRoleById(id) {
    return axios.get(`${API_URL}/roles/${id}`)
  },

  // Tạo vai trò mới
  createRole(roleData) {
    return axios.post(`${API_URL}/roles`, roleData)
  },

  // Cập nhật vai trò
  updateRole(id, roleData) {
    return axios.put(`${API_URL}/roles/${id}`, roleData)
  },

  // Xóa vai trò
  deleteRole(id) {
    return axios.delete(`${API_URL}/roles/${id}`)
  }
} 