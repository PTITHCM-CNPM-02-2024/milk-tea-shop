import apiClient from './api';

export default {
  // Lấy tổng quan báo cáo
  getReportSummary(params) {
    return apiClient.get('/reports/summary', { params });
  },

  // Lấy báo cáo doanh thu theo thời gian
  getRevenueByTime(params) {
    return apiClient.get('/reports/revenue/time', { params });
  },

  // Lấy báo cáo top sản phẩm bán chạy
  getTopProducts(params) {
    return apiClient.get('/reports/products/top', { params });
  },

  // Lấy báo cáo doanh thu theo danh mục
  getRevenueByCategory(params) {
    return apiClient.get('/reports/revenue/category', { params });
  },

  // Lấy báo cáo chi tiết đơn hàng
  getOrderDetails(params) {
    return apiClient.get('/reports/orders', { params });
  },

  // Lấy báo cáo hoạt động theo nhân viên
  getEmployeeActivity(params) {
    return apiClient.get('/reports/employees/activity', { params });
  },

  // Xuất báo cáo ra file
  exportReport(reportType, params) {
    return apiClient.get(`/reports/export/${reportType}`, {
      params,
      responseType: 'blob'
    });
  }
}; 