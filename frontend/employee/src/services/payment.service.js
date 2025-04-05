import apiClient from './api';

export default {
    // Lấy tất cả phương thức thanh toán
    getAllPaymentMethods() {
        return apiClient.get('/payment-methods');
    }
};