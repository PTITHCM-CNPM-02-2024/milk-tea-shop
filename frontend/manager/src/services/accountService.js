import api from './api'

export const accountService = {
  // Lấy danh sách tài khoản với phân trang
  getAccounts(page = 0, size = 10, searchQuery = '', roleId = null, status = null) {
    return api.get('/accounts', {
      params: { 
        page, 
        size,
        search: searchQuery,
        roleId: roleId,
        status: status
      }
    })
  },

  // Lấy thông tin chi tiết tài khoản
  getAccountById(id) {
    return api.get(`/accounts/${id}`)
  },

  // Tạo tài khoản mới
  createAccount(accountData) {
    return api.post('/accounts', accountData)
  },

  // Cập nhật tài khoản
  updateAccount(id, accountData) {
    return api.put(`/accounts/${id}`, accountData)
  },

  // Cập nhật tên đăng nhập
  updateUsername(id, data) {
    return api.put(`/accounts/${id}/username`, data)
  },

  // Xóa tài khoản
  deleteAccount(id) {
    return api.delete(`/accounts/${id}`)
  },

  // Lấy danh sách vai trò
  getRoles() {
    return api.get('/roles')
  },

  // Khóa/Mở khóa tài khoản
  toggleAccountLock(id, isLocked) {
    return api.put(`/accounts/${id}/lock?value=${isLocked}`)
  },

  // Kích hoạt/Vô hiệu hóa tài khoản
  toggleAccountActive(id, isActive) {
    return api.put(`/accounts/${id}/active`, { active: isActive })
  },

  // Thay đổi mật khẩu tài khoản
  changePassword(id, oldPassword, newPassword, confirmPassword) {
    return api.put(`/accounts/${id}/password`, null, {
      params: {
        oldPassword,
        newPassword,
        confirmPassword
      }
    })
  },

  // Thay đổi vai trò tài khoản
  changeRole(id, roleId) {
    return api.put(`/accounts/${id}/role`, null, {
      params: {
        value: roleId
      }
    })
  }
}
