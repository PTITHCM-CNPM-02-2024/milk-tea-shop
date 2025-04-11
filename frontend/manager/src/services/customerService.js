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
    const request = {
      name: data.name,
      description: data.description,
      discountUnit: data.discountUnit,
      discountValue: data.discountValue,
      requiredPoint: data.requiredPoints,
      validUntil: data.validUntil
    }
    return api.post('/memberships', request)
  },

  updateMembershipType(id, data) {
    const request = {
      name: data.name,
      description: data.description,
      discountUnit: data.discountUnit,
      discountValue: data.discountValue,
      requiredPoint: data.requiredPoints,
      validUntil: data.validUntil,
      active: data.isActive
    }
    return api.put(`/memberships/${id}`, request)
  },

  deleteMembershipType(id) {
    return api.delete(`/memberships/${id}`)
  }
} 