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
  }
} 