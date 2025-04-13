import api from './api'

export const roleService = {
  // Lấy danh sách vai trò với phân trang
  getAllRoles(page = 0, size = 10) {
    return api.get('/roles', {
      params: { page, size }
    })
  },

  // Lấy thông tin chi tiết vai trò
  getRoleById(id) {
    return api.get(`/roles/${id}`)
  },

  // Tạo vai trò mới
  createRole(roleData) {
    return api.post('/roles', roleData)
  },

  // Cập nhật vai trò
  updateRole(id, roleData) {
    return api.put(`/roles/${id}`, roleData)
  },

  // Xóa vai trò
  deleteRole(id) {
    return api.delete(`/roles/${id}`)
  }
} 