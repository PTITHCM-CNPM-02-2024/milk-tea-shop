import apiClient from './api';

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
  }
};