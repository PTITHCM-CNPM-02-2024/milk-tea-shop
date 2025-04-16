import apiClient from './api';
import axios from 'axios';

export default {
  // Tạo đơn hàng mới
  createOrder(orderData) {
    return apiClient.post('/orders', orderData);
  },
  
  // Hủy đơn hàng
  cancelOrder(orderId) {
    return apiClient.put(`/orders/${orderId}/cancel`);
  },
  
  // Khởi tạo thanh toán
  initiatePayment(paymentData) {
    return apiClient.post('/payments/initiate', paymentData);
  },
  
  // Hoàn tất thanh toán
  completePayment(paymentId, methodId, transactionData) {
    return apiClient.post(`/payments/${paymentId}/${methodId}/complete`, transactionData);
  },
  
  // Lấy tất cả khuyến mãi
  getAllDiscounts() {
    return apiClient.get('/discounts');
  },
  
  // Lấy khuyến mãi theo mã giảm giá
  getDiscountByCoupon(couponCode) {
    return apiClient.get(`/discounts/coupon/${couponCode}`);
  },

    // Tính toán đơn hàng
    calculateOrder(orderData) {
    console.log(orderData);
      return apiClient.post('/orders/utilities/calculate', orderData);
  },

  // Lấy danh sách đơn hàng và bàn theo ID nhân viên
  getOrderTables(employeeId, page = 0, size = 10) {
    return apiClient.get(`/employees/${employeeId}/orders/order-tables`, {
      params: {
        page,
        size
      }
    });
  },
  
  // API checkout bàn (cần bổ sung khi có thông tin API cụ thể)
  checkoutTable(orderId) {
    return apiClient.put(`/orders/${orderId}/checkout`);
  },
  
  // Lấy chi tiết đơn hàng theo ID
  getOrderById(orderId) {
    return apiClient.get(`/orders/${orderId}`);
  },
};