import api from './api'

export const paymentService = {
  // Lấy danh sách thanh toán
  getAllPayments(page = 0, size = 10) {
    return api.get(`/payments?page=${page}&size=${size}`)
  },

  // Lấy chi tiết thanh toán
  getPaymentById(paymentId) {
    return api.get(`/payments/${paymentId}`)
  },

  // Thêm thanh toán cho đơn hàng
  addPaymentToOrder(orderId, paymentData) {
    return api.post(`/orders/${orderId}/payments`, paymentData)
  },

  // Lấy báo cáo thanh toán theo tháng
  getPaymentReport(month, year) {
    return api.get(`/payments/report?month=${month}&year=${year}`)
  },

  // Lấy danh sách phương thức thanh toán
  getAllPaymentMethods() {
    return api.get('/payment-methods')
  },

  // Lấy chi tiết phương thức thanh toán
  getPaymentMethodById(id) {
    return api.get(`/payment-methods/${id}`)
  },

  // Tạo phương thức thanh toán mới
  createPaymentMethod(data) {
    return api.post('/payment-methods', data)
  },

  // Cập nhật phương thức thanh toán
  updatePaymentMethod(id, data) {
    return api.put(`/payment-methods/${id}`, data)
  },

  // Xóa phương thức thanh toán
  deletePaymentMethod(id) {
    return api.delete(`/payment-methods/${id}`)
  }
}

export default paymentService 