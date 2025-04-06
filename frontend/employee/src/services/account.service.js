import apiClient from './api.js';

const AccountService = {
  // Lấy thông tin người dùng (nhân viên)
  getUserInfo(id) {
    return apiClient.get(`/accounts/${id}/user-info`);
  },
  
  // Lấy thông tin tài khoản
  getAccountInfo(id) {
    return apiClient.get(`/accounts/${id}`);
  },
  
  // Cập nhật tài khoản (username)
  updateAccount(id, username) {
    return apiClient.put(`/accounts/${id}`, {
      username
    });
  },
  
  // Đổi mật khẩu
  changePassword(id, newPassword, confirmPassword) {
    return apiClient.put(`/accounts/${id}/change-password`, {
      newPassword,
      confirmPassword
    });
  },
  
  // Cập nhật thông tin nhân viên
  updateEmployee(id, employeeData) {
    return apiClient.put(`/employees/${id}`, employeeData);
  }
};

export default AccountService; 