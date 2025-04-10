import api from './api'

export const accountService = {
  // Lấy danh sách tài khoản với phân trang
  getAccounts(page = 0, size = 10) {
    return api.get('/accounts', {
      params: { page, size }
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
    return api.patch(`/accounts/${id}/lock`, { isLocked })
  },

  // Kích hoạt/Vô hiệu hóa tài khoản
  toggleAccountActive(id, isActive) {
    return api.patch(`/accounts/${id}/activate`, { isActive })
  },

  // Thay đổi mật khẩu tài khoản
  changePassword(id, oldPassword, newPassword) {
    return api.put(`/accounts/${id}/password`, null, {
      params: {
        oldPassword,
        newPassword
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
