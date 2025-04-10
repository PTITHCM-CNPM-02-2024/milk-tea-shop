import api from './api'

export const customerService = {
  // API cho quản lý khách hàng
  getCustomers(page = 0, size = 10) {
    return api.get('/customers', {
      params: { page, size }
    })
  },

  getCustomerById(id) {
    return api.get(`/customers/${id}`)
  },

  createCustomer(customerData) {
    return api.post('/customers', customerData)
  },

  updateCustomer(id, customerData) {
    return api.put(`/customers/${id}`, customerData)
  },

  deleteCustomer(id) {
    return api.delete(`/customers/${id}`)
  },

  // API cho chương trình thành viên
  getMembershipTypes(page = 0, size = 10) {
    return api.get('/memberships', {
      params: { page, size }
    })
  },

  getMembershipTypeById(id) {
    return api.get(`/memberships/${id}`)
  },

  createMembershipType(data) {
    return api.post('/memberships', data)
  },

  updateMembershipType(id, data) {
    return api.put(`/memberships/${id}`, data)
  },

  deleteMembershipType(id) {
    return api.delete(`/memberships/${id}`)
  }
} 