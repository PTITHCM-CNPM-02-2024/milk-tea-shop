import api from './api'

export const employeeService = {
  // Lấy danh sách nhân viên với phân trang
  getEmployees(page = 0, size = 10) {
    return api.get('/employees', {
      params: { page, size }
    })
  },
  
  // Lấy thông tin chi tiết nhân viên
  getEmployeeById(id) {
    return api.get(`/employees/${id}`)
  },
  
  // Tạo nhân viên mới
  createEmployee(employeeData) {
    return api.post('/employees', employeeData)
  },
  
  // Cập nhật thông tin nhân viên
  updateEmployee(id, employeeData) {
    return api.put(`/employees/${id}`, employeeData)
  },
  
  // Xóa nhân viên
  deleteEmployee(id) {
    return api.delete(`/employees/${id}`)
  }
} 