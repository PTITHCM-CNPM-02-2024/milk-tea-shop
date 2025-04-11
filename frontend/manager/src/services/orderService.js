import api from './api'

export const orderService = {
  // Lấy danh sách đơn hàng
  getAllOrders(page = 0, size = 10) {
    return api.get(`/orders?page=${page}&size=${size}`)
  },

  // Lấy chi tiết đơn hàng
  getOrderById(orderId) {
    return api.get(`/orders/${orderId}`)
  },

  // Cập nhật trạng thái đơn hàng
  updateOrderStatus(orderId, status) {
    return api.put(`/orders/${orderId}/status`, { status })
  },

  // Tạo đơn hàng mới
  createOrder(orderData) {
    return api.post('/orders', orderData)
  }
}

export default orderService
