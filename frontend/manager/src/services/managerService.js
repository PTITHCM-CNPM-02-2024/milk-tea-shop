import api from './api'

export const managerService = {
  // Lấy thông tin chi tiết manager
  getManagerById(id) {
    return api.get(`/managers/${id}`)
  },

  // Cập nhật thông tin manager
  updateManager(id, managerData) {
    const request = {
      email: managerData.email,
      firstName: managerData.firstName,
      lastName: managerData.lastName,
      gender: managerData.gender,
      phone: managerData.phone,
      username: managerData.username
    }
    return api.put(`/managers/${id}`, request)
  },
  
  // Tạo mới manager
  createManager(managerData) {
    const request = {
      email: managerData.email,
      firstName: managerData.firstName,
      lastName: managerData.lastName,
      gender: managerData.gender,
      phone: managerData.phone,
      username: managerData.username,
      accountId: managerData.accountId
    }
    return api.post('/managers', request)
  }
}
