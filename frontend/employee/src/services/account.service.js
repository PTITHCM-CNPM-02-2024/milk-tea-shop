import apiClient from './api.js';

const AccountService = {
  // Lấy thông tin nhân viên
  getEmployeeInfo(id) {
    return apiClient.get(`/employees/${id}`);
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
  
  // Đổi mật khẩu - cập nhật theo API mới
  changePassword(id, oldPassword, newPassword, confirmPassword) {
    return apiClient.put(`/accounts/${id}/password?oldPassword=${oldPassword}&newPassword=${newPassword}&confirmPassword=${confirmPassword}`);
  },
  
  // Cập nhật thông tin nhân viên - giữ lại nhưng không sử dụng
  updateEmployee(id, employeeData) {
    return apiClient.put(`/employees/${id}`, employeeData);
  }
};

export default AccountService; 