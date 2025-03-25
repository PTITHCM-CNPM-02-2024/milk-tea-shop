import apiClient from './api';

export default {
  // Lấy tất cả khách hàng
  getAllCustomers() {
    return apiClient.get('/customers');
  },
  
  // Lấy thông tin khách hàng theo ID
  getCustomerById(id) {
    return apiClient.get(`/customers/${id}`);
  },
  
  // Tìm kiếm khách hàng theo số điện thoại
  getCustomerByPhone(phone) {
    return apiClient.get(`/customers/search/phone?phone=${phone}`);
  },
  
  // Tạo khách hàng mới
  createCustomer(customer) {
    return apiClient.post('/customers', customer);
  },
  
  // Cập nhật thông tin khách hàng
  updateCustomer(id, customer) {
    return apiClient.put(`/customers/${id}`, customer);
  },
  
  // Cập nhật hạng thành viên cho khách hàng
  updateCustomerMembership(id, membershipId) {
    return apiClient.put(`/customers/${id}/membership?membershipId=${membershipId}`);
  },
  
  // Lấy tất cả loại thành viên
  getAllMemberships() {
    return apiClient.get('/memberships');
  }
};